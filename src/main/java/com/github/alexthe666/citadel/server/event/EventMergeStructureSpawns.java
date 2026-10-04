package com.github.alexthe666.citadel.server.event;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

public class EventMergeStructureSpawns extends BaseEvent {
    public interface Callback {
        void onMergeStructureSpawns(EventMergeStructureSpawns event);
    }

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onMergeStructureSpawns(event);
        }
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onMergeStructureSpawns(this);
    }

    private StructureManager structureManager;
    private BlockPos pos;
    private MobCategory category;
    private WeightedRandomList<MobSpawnSettings.SpawnerData> structureSpawns;
    private WeightedRandomList<MobSpawnSettings.SpawnerData> biomeSpawns;
    private TriState result = TriState.DEFAULT;

    public EventMergeStructureSpawns(StructureManager structureManager, BlockPos pos, MobCategory category, WeightedRandomList<MobSpawnSettings.SpawnerData> structureSpawns, WeightedRandomList<MobSpawnSettings.SpawnerData> biomeSpawns) {
        this.structureManager = structureManager;
        this.pos = pos;
        this.category = category;
        this.structureSpawns = structureSpawns;
        this.biomeSpawns = biomeSpawns;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public BlockPos getPos() {
        return pos;
    }

    public MobCategory getCategory() {
        return category;
    }

    public boolean isStructureTagged(TagKey<Structure> tagKey) {
        return structureManager.getStructureWithPieceAt(pos, tagKey).isValid();
    }

    public WeightedRandomList<MobSpawnSettings.SpawnerData> getStructureSpawns() {
        return structureSpawns;
    }

    public void setStructureSpawns(WeightedRandomList<MobSpawnSettings.SpawnerData> spawns) {
        structureSpawns = spawns;
    }

    public void mergeSpawns() {
        List<MobSpawnSettings.SpawnerData> list = new ArrayList<>(biomeSpawns.unwrap());
        for (MobSpawnSettings.SpawnerData structureSpawn : structureSpawns.unwrap()) {
            if (!list.contains(structureSpawn)) {
                list.add(structureSpawn);
            }
        }
        this.setStructureSpawns(WeightedRandomList.create(list));
    }

    public WeightedRandomList<MobSpawnSettings.SpawnerData> getBiomeSpawns() {
        return biomeSpawns;
    }

    public TriState getResult() {
        return this.result;
    }

    public void setResult(TriState result) {
        this.result = result;
    }
}
