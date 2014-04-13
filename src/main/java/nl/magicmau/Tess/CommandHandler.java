package nl.magicmau.Tess;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.exception.CommandException;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public class CommandHandler {

    private final Citizens plugin;

    public CommandHandler(Citizens plugin) {
        this.plugin = plugin;
    }

    @Command(
            aliases = { "tess" }, usage = "reload",
            desc = "Reloads Javascript files from the scripts folder.",
            modifiers = { "reload" }
    )
    public void reload(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        Tess tess = (Tess) plugin.getServer().getPluginManager().getPlugin("Tess");

        tess.getLogger().info("Reloading scripts!");

        tess.reloadScripts();
    }
}
