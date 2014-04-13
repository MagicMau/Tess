package nl.magicmau.Tess;

import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.SpeechController;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import nl.magicmau.Tess.midi.MidiUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.UUID;

/**
 * Copyright (c) 2014, MagicMau (magicmau@gmail.com).
 */
public class TessNPC implements NPC {
    private final NPC npc;
    private final TessTrait trait;
    private final Tess tess;

    public TessNPC(TessTrait npcTrait, Tess tess) {
        this.npc = npcTrait.getNPC();
        this.trait = npcTrait;
        this.tess = tess;
    }

    public void triggerEvent(String eventName, Object... args) {
        trait.triggerEvent(eventName, args);
    }

    public void chatTo(Player player, String message) {
        SpeechContext context = new SpeechContext(this, message, player);
        npc.getDefaultSpeechController().speak(context, "chat");
    }

    public void walkTo(Location location) {
        double distance = npc.getEntity().getLocation().distance(location);
        npc.getNavigator().getDefaultParameters().range((float)distance + 10);
        npc.getNavigator().setTarget(location);
    }

    public void playMidi(String name) {
        String path = tess.getDataFolder() + File.separator + "midi" + File.separator + name + ".mid";
        File file = new File(path);
        if (file.exists()) {
            MidiUtil.playMidi(file, 1, npc.getEntity().getLocation());
        }
    }

    public Location getLocation() {
        return npc.getEntity().getLocation();
    }

    @Override
    public void addTrait(Class<? extends Trait> aClass) {
        npc.addTrait(aClass);
    }

    @Override
    public void addTrait(Trait trait) {
        npc.addTrait(trait);
    }

    @Override
    public NPC clone() {
        return npc.clone();
    }

    @Override
    public MetadataStore data() {
        return npc.data();
    }

    @Override
    public boolean despawn() {
        return npc.despawn();
    }

    @Override
    public boolean despawn(DespawnReason despawnReason) {
        return npc.despawn(despawnReason);
    }

    @Override
    public void destroy() {
        npc.destroy();
    }

    @Override
    public void faceLocation(Location location) {
        npc.faceLocation(location);
    }

    @Override
    @Deprecated
    public LivingEntity getBukkitEntity() {
        return npc.getBukkitEntity();
    }

    @Override
    public GoalController getDefaultGoalController() {
        return npc.getDefaultGoalController();
    }

    @Override
    public SpeechController getDefaultSpeechController() {
        return npc.getDefaultSpeechController();
    }

    @Override
    public Entity getEntity() {
        return npc.getEntity();
    }

    @Override
    public String getFullName() {
        return npc.getFullName();
    }

    @Override
    public int getId() {
        return npc.getId();
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public Navigator getNavigator() {
        return npc.getNavigator();
    }

    @Override
    public NPCRegistry getOwningRegistry() {
        return npc.getOwningRegistry();
    }

    @Override
    public Location getStoredLocation() {
        return npc.getStoredLocation();
    }

    @Override
    public <T extends Trait> T getTrait(Class<T> tClass) {
        return npc.getTrait(tClass);
    }

    @Override
    public Iterable<Trait> getTraits() {
        return npc.getTraits();
    }

    @Override
    public UUID getUniqueId() {
        return npc.getUniqueId();
    }

    @Override
    public boolean hasTrait(Class<? extends Trait> aClass) {
        return npc.hasTrait(aClass);
    }

    @Override
    public boolean isFlyable() {
        return npc.isFlyable();
    }

    @Override
    public boolean isProtected() {
        return npc.isProtected();
    }

    @Override
    public boolean isSpawned() {
        return npc.isSpawned();
    }

    @Override
    public void load(DataKey dataKey) {
        npc.load(dataKey);
    }

    @Override
    public void removeTrait(Class<? extends Trait> aClass) {
        npc.removeTrait(aClass);
    }

    @Override
    public void save(DataKey dataKey) {
        npc.save(dataKey);
    }

    @Override
    public void setBukkitEntityType(EntityType entityType) {
        npc.setBukkitEntityType(entityType);
    }

    @Override
    public void setFlyable(boolean b) {
        npc.setFlyable(b);
    }

    @Override
    public void setName(String s) {
        npc.setName(s);
    }

    @Override
    public void setProtected(boolean b) {
        npc.setProtected(b);
    }

    @Override
    public boolean spawn(Location location) {
        return npc.spawn(location);
    }

    @Override
    public void teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
        npc.teleport(location, teleportCause);
    }
}
