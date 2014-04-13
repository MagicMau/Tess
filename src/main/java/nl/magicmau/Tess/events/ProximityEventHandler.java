package nl.magicmau.Tess.events;

import net.citizensnpcs.api.npc.NPC;
import nl.magicmau.Tess.Tess;
import nl.magicmau.Tess.TessNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public class ProximityEventHandler extends TessEventHandler {
    public ProximityEventHandler(Tess tess) {
        super(tess);
    }

    @EventHandler
    public void proximityTrigger(PlayerMoveEvent event) {
        for (TessNPC npc : tess.getTessNPCs()) {
            checkProximity(npc, event);
        }
    }

    private void checkProximity(TessNPC npc, PlayerMoveEvent event) {

        //
        // Make sure that the player actually moved to a different block.
        //
        if (!event.getTo().getBlock().equals(event.getFrom().getBlock())) {
            //
            // Get block location
            //
            Location toBlockLocation = event.getTo().getBlock().getLocation();

            //
            // If this NPC is more than the maxProximityDistance, skip it, unless
            // the Player hasn't yet triggered an 'Exit Proximity' after entering.
            //
            if (!isCloseEnough(event.getPlayer(), npc) && hasExitedProximityOf(event.getPlayer(), npc))
                return;

            Location npcLocation = npc.getLocation();
            double entryRadius = 5;
            double exitRadius = 5;
            double moveRadius = 5;

            //
            // If the Player switches worlds while in range of an NPC, trigger still needs to
            // fire since technically they have exited proximity. Let's check that before
            // trying to calculate a distance between the Player and NPC, which will throw
            // an exception if worlds do not match.
            //
            boolean playerChangedWorlds = false;
            if (npcLocation.getWorld() != event.getPlayer().getWorld())
                playerChangedWorlds = true;

            //
            // If the user is outside the range, and was previously within the
            // range, then execute the "Exit" script.
            //
            // If the user entered the range and were not previously within the
            // range, then execute the "Entry" script.
            //
            // If the user was previously within the range and moved, then execute
            // the "Move" script.
            //
            boolean exitedProximity = hasExitedProximityOf(event.getPlayer(), npc);
            double distance = 0;
            if (!playerChangedWorlds) distance = npcLocation.distance(toBlockLocation);

            if (!exitedProximity && (playerChangedWorlds || distance >= exitRadius)) {
                // Remember that NPC has exited proximity.
                exitProximityOf(event.getPlayer(), npc);
                npc.triggerEvent("onExitProximity", event);
            }
            else if (exitedProximity && distance <= entryRadius) {
                // Remember that Player has entered proximity of the NPC
                enterProximityOf(event.getPlayer(), npc);
                npc.triggerEvent("onEnterProximity", event);
            }
            else if (!exitedProximity && distance <= moveRadius) {
                npc.triggerEvent("onMoveProximity", event);
            }
        }
    }

    /**
     * Checks if the Player in Proximity is close enough to be calculated.
     *
     * @param player the Player
     * @param npc the NPC
     * @return true if within maxProximityDistance in all directions
     *
     */
    private boolean isCloseEnough(Player player, NPC npc) {
        Location pLoc = player.getLocation();
        Location nLoc = npc.getEntity().getLocation();
        if (Math.abs(pLoc.getX() - nLoc.getX()) > 50) return false;
        if (Math.abs(pLoc.getY() - nLoc.getY()) > 50) return false;
        if (Math.abs(pLoc.getZ() - nLoc.getZ()) > 50) return false;
        return true;
    }

    private static Map<Player, Set<Integer>> proximityTracker = new ConcurrentHashMap<Player, Set<Integer>>(8, 0.9f, 1);

    //
    // Ensures that a Player who has entered proximity of an NPC also fires Exit Proximity.
    //
    private boolean hasExitedProximityOf(Player player, NPC npc) {
        // If Player hasn't entered proximity, it's not in the Map. Return true, must be exited.
        Set<Integer> existing = proximityTracker.get(player);
        if (existing == null) return true;
        // If Player has no entry for this NPC, return true.
        if (!existing.contains(npc.getId())) return true;
        // Entry is present, NPC has not yet triggered exit proximity.
        return false;
    }

    /**
     * Called when a 'Enter Proximity' has been called to make sure an exit
     * proximity will be called.
     *
     * @param player the Player
     * @param npc the NPC
     */
    private void enterProximityOf(Player player, NPC npc) {
        Set<Integer> npcs = proximityTracker.get(player);
        if (npcs == null) {
            npcs = new HashSet<Integer>();
            proximityTracker.put(player, npcs);
        }
        npcs.add(npc.getId());
    }

    /**
     * Called when an 'Exit Proximity' has been called. Once successfully exited,
     * a Player can enter proximity again.
     *
     * @param player the Player
     * @param npc the NPC
     */
    private void exitProximityOf(Player player, NPC npc) {
        Set<Integer> npcs = proximityTracker.get(player);
        if (npcs == null) {
            npcs = new HashSet<Integer>();
            proximityTracker.put(player, npcs);
        }
        npcs.remove(npc.getId());
    }

}
