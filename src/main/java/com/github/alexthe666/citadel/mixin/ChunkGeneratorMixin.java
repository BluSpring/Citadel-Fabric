package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.event.EventMergeStructureSpawns;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;

import net.fabricmc.fabric.api.util.TriState;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @ModifyReturnValue(at = @At("RETURN"), remap = CitadelConstants.REMAPREFS, method = "getMobsAt(Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/util/random/WeightedRandomList;")
    private WeightedRandomList<MobSpawnSettings.SpawnerData> citadel_getMobsAt(WeightedRandomList<MobSpawnSettings.SpawnerData> original, Holder<Biome> biome, StructureManager structureManager, MobCategory mobCategory, BlockPos pos) {
        WeightedRandomList<MobSpawnSettings.SpawnerData> biomeSpawns = biome.value().getMobSettings().getMobs(mobCategory);
        if (biomeSpawns != original) {
            EventMergeStructureSpawns event = new EventMergeStructureSpawns(structureManager, pos, mobCategory, original, biomeSpawns);
            event.sendEvent();
            if (event.getResult() == TriState.TRUE) {
                return event.getStructureSpawns();
            }
        }

        return original;
    }

}
