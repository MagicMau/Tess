package nl.magicmau.Tess;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import nl.magicmau.Tess.events.ChatEventHandler;
import nl.magicmau.Tess.events.ClickEventHandler;
import nl.magicmau.Tess.events.ProximityEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2014, MagicMau (mau@magicmau.nl).
 */
public class Tess extends JavaPlugin {

    Citizens citizens;
    String scriptsFolder;
    ScriptEngineManager scriptEngineManager;
    ScriptEngine js;

    private ConcurrentHashMap<NPC, ScriptableTess> npcs;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        citizens = (Citizens) Bukkit.getServer().getPluginManager().getPlugin("Citizens");
        if(citizens == null || !citizens.isEnabled()) {
            getLogger().warning("Citizens does not seem to be activated! Deactivating Tess!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        npcs = new ConcurrentHashMap<NPC, ScriptableTess>();

        // Register commandHandler with Citizens2
        commandHandler = new CommandHandler(citizens);
        citizens.registerCommandClass(CommandHandler.class);

        // register trait
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TessTrait.class).withName("Tess"));

        scriptsFolder = getDataFolder().toString().replaceAll("\\\\", "/") + "/scripts";
        // make sure it exists
        new File(scriptsFolder).mkdirs();

        // ensure require.js is in the scripts folder, 'cause we need it
        if (!new File(getDataFolder() + "/require.js").exists()) {
            try {
                String sourceFile = URLDecoder.decode(Tess.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
                extractFile(new File(sourceFile), "require.js", getDataFolder() + "/scripts/");
            } catch (UnsupportedEncodingException e) {
                getLogger().severe("Error while extracting require.js: " + e.getMessage());
            }

        }

        // register event handlers
        new ClickEventHandler(this);
        new ChatEventHandler(this);
        new ProximityEventHandler(this);

        // Initialize the script engine
        scriptEngineManager = new ScriptEngineManager();

        // Now prepare the scripts
        reloadScripts();

    }

    public void reloadScripts() {
        js = scriptEngineManager.getEngineByName("javascript");

        // prepare CommonJS
        try {
            js.eval("load('nashorn:mozilla_compat.js');");
            js.eval("var Require = load('" + scriptsFolder + "/require.js');");
            js.eval("var require = Require('" + scriptsFolder + "/', ['lib']);");
        } catch (ScriptException e) {
            getLogger().severe("Error initializing scripts: " + e.getMessage());
        }

        for (ScriptableTess npc : npcs.values()) {
            npc.initializeScripts(js);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (citizens == null)
            citizens = (Citizens) getServer().getPluginManager().getPlugin("Citizens");

        return citizens.onCommand(sender, command, label, args);
    }

    /**
     * Extract a file from a zip or jar (shamelessly copied from Denizen).
     * @param jarFile The zip/jar file to use
     * @param fileName Which file to extract
     * @param destDir Where to extract it to
     */
    private void extractFile(File jarFile, String fileName, String destDir) {
        java.util.jar.JarFile jar = null;

        try {
            jar = new java.util.jar.JarFile(jarFile);
            java.util.Enumeration myEnum = jar.entries();
            while (myEnum.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) myEnum.nextElement();
                if (file.getName().equalsIgnoreCase(fileName)) {
                    java.io.File f = new java.io.File(destDir + "/" + file.getName());
                    if (file.isDirectory()) {
                        continue;
                    }
                    java.io.InputStream is = jar.getInputStream(file);
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
                    while (is.available() > 0)
                        fos.write(is.read());
                    fos.close();
                    is.close();
                    return;
                }
            }
            getLogger().severe(fileName + " not found in the jar!");

        } catch (IOException e) {
            getLogger().severe("Error while extracting file: " + e.getMessage());

        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    getLogger().severe("Error while extracting file (2): " + e.getMessage());
                }
            }
        }
    }

    public void registerScriptableTess(ScriptableTess scriptableTess) {
        npcs.put(scriptableTess.getNPC(), scriptableTess);
    }

    public TessNPC findTessNPC(NPC npc) {
        ScriptableTess scriptableTess = npcs.get(npc);
        if (scriptableTess != null)
            return scriptableTess.getTessNPC();
        return null;
    }

    public List<TessNPC> getTessNPCs() {
        return npcs.values().parallelStream().map(t -> t.getTessNPC()).collect(Collectors.toList());
    }
}
