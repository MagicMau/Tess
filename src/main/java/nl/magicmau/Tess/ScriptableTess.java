package nl.magicmau.Tess;

import net.citizensnpcs.api.npc.NPC;

import javax.script.ScriptEngine;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public interface ScriptableTess {

    void initializeScripts(ScriptEngine js);

    NPC getNPC();

    TessNPC getTessNPC();
}
