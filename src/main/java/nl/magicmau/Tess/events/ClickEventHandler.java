package nl.magicmau.Tess.events;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import nl.magicmau.Tess.Tess;
import nl.magicmau.Tess.TessNPC;
import org.bukkit.event.EventHandler;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public class ClickEventHandler extends TessEventHandler {

    public ClickEventHandler(Tess tess) {
        super(tess);
    }

    @EventHandler
    public void clickTrigger(NPCRightClickEvent event) {
        TessNPC npc = findTessNPC(event.getNPC());

        if (npc == null)
            return;

        npc.triggerEvent("onClick", event);
    }

}
