package nl.magicmau.Tess;

import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public class TessTrait extends Trait implements ScriptableTess {
    private final Tess tess;
    private final String npcVar;
    private ScriptEngine js;

    private Object npcObject;

    private static final Object mutex = new Object();
    private static int count = 1;

    public TessTrait() {
        super("Tess");
        tess = (Tess) Bukkit.getServer().getPluginManager().getPlugin("Tess");

        synchronized (mutex) {
            npcVar = "npc" + String.valueOf(count++);
        }
    }

    @Override
    public void linkToNPC(NPC npc) {
        super.linkToNPC(npc);
        tess.registerScriptableTess(this);
    }

    @Override
    public void initializeScripts(ScriptEngine js) {
        this.js = js;
        String moduleName = npc.getName().replace(' ', '_');

        try {
            js.eval("var " + npcVar + " = require('./" + moduleName + "');");
            Object npc = js.get(npcVar);
            Invocable invocable = (Invocable)js;
            npcObject = invocable.invokeMethod(npc, "createNPC", getTessNPC());
        } catch (Exception e) {
            tess.getLogger().severe("Could not initialize NPC " + moduleName + ": " + e.getMessage());
        }
    }

    public void triggerEvent(String eventName, Object... args) {
        Invocable invocable = (Invocable)js;
        try {
            invocable.invokeMethod(npcObject, eventName, args);
        } catch (Exception e) {
            tess.getLogger().info(e.getMessage());
        }
    }

    public TessNPC getTessNPC() {
        return new TessNPC(this, tess);
    }

    @Override
    public void onAttach() {
        if (this.js == null)
            initializeScripts(tess.js);
    }

    @Override
    public void onSpawn() {
        triggerEvent("onSpawn");
    }

    @Override
    public void onDespawn() {
        triggerEvent("onDespawn");
    }

    @Override
    public void onRemove() {
        triggerEvent("onRemove");
    }



    @EventHandler
    public void navigationCompleteTrigger(NavigationCompleteEvent e) {
        triggerEvent("onNavigationComplete", e);
    }

    @EventHandler
    public void navigationCancelledTrigger(NavigationCancelEvent e) {
        navigationCompleteTrigger(e);
    }



}
