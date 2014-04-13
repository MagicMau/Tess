package nl.magicmau.Tess.events;

import nl.magicmau.Tess.Tess;
import nl.magicmau.Tess.TessNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.Vector;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public class ChatEventHandler extends TessEventHandler {
    public ChatEventHandler(Tess tess) {
        super(tess);
    }

    @EventHandler
    public void chatTrigger(AsyncPlayerChatEvent event) {
        for (TessNPC npc : tess.getTessNPCs()) {
            checkChat(npc, event);
        }
    }

    private void checkChat(TessNPC npc, AsyncPlayerChatEvent event) {
        Location myLoc = npc.getLocation();
        Location playerLoc = event.getPlayer().getLocation();

        // Check if in range
        double distance = myLoc.distanceSquared(playerLoc);
        if (distance < 3 * 3) {
            // Check if in line of sight
            if (event.getPlayer().hasLineOfSight(npc.getEntity())) {
                // Check if player is looking at NPC
                if (isFacingLocation(event.getPlayer().getLocation(), npc.getEntity().getLocation(), 45)) {
                    // because we are handling the event now, don't let Minecraft handle it
                    event.setCancelled(true);
                    // show how the player is talking to the NPC
                    talkToNPC(npc, event.getPlayer(), event.getMessage());
                    // Now, fire event
                    npc.triggerEvent("onChat", event);
                }
            }
        }
    }

    private void talkToNPC(TessNPC npc, Player player, String message) {
        String talkMessage = "You -> " + npc.getName() + ": " + message;
        String bystanderMessage = player.getName() + " -> " + npc.getName() + ": " + message;


        // Send message to player
        player.sendMessage(talkMessage);

        // Send message to bystanders
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target != player)
                if (target.getWorld().equals(player.getWorld())
                        && target.getLocation().distance(player.getLocation()) <= 4)
                    target.sendMessage(bystanderMessage);
        }
    }

    /**
     * Checks if a Location's yaw is facing another Location.
     *
     * Note: do not use a player's location as the first argument,
     *       because player yaws need to modified. Use the method
     *       below this one instead.
     *
     * @param  from  The Location we check.
     * @param  at  The Location we want to know if the first Location's yaw
     *             is facing
     * @param  degreeLimit  How many degrees can be between the direction the
     *                         first location's yaw is facing and the direction
     *                         we check if it is facing.
     *
     * @return  Returns a boolean.
     */
    private boolean isFacingLocation(Location from, Location at, float degreeLimit) {

        double currentYaw = normalizeYaw(from.getYaw());

        double requiredYaw = normalizeYaw(getYaw(at.toVector().subtract(
                from.toVector()).normalize()));

        return (Math.abs(requiredYaw - currentYaw) < degreeLimit ||
                Math.abs(requiredYaw + 360 - currentYaw) < degreeLimit ||
                Math.abs(currentYaw + 360 - requiredYaw) < degreeLimit);
    }

    /**
     * Normalizes Mincraft's yaws (which can be negative or can exceed 360)
     * by turning them into proper yaw values that only go from 0 to 359.
     *
     * @param  yaw  The original yaw.
     *
     * @return  The normalized yaw.
     */

    private float normalizeYaw(float yaw) {
        yaw = yaw  % 360;
        if (yaw < 0) yaw += 360.0;
        return yaw;
    }

    /**
     * Converts a vector to a yaw.
     *
     * Thanks to bergerkiller.
     *
     * @param  vector  The vector you want to get a yaw from.
     *
     * @return  The yaw.
     */

    private float getYaw(Vector vector) {
        double dx = vector.getX();
        double dz = vector.getZ();
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (-yaw * 180 / Math.PI);
    }

}
