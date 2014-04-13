package nl.magicmau.Tess.events;

import net.citizensnpcs.api.npc.NPC;
import nl.magicmau.Tess.Tess;
import nl.magicmau.Tess.TessNPC;
import org.bukkit.event.Listener;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public abstract class TessEventHandler implements Listener {

    protected final Tess tess;

    public TessEventHandler(Tess tess) {
        this.tess = tess;
        tess.getServer().getPluginManager().registerEvents(this, tess);
    }

    protected TessNPC findTessNPC(NPC npc) {
        return tess.findTessNPC(npc);
    }
}
