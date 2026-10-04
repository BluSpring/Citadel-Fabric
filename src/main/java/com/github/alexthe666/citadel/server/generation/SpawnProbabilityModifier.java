package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.mixin.fabric.SpawnSettingsContextImplAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;

public class SpawnProbabilityModifier {
    public static void setup() {
        BiomeModifications.create(ResourceLocation.fromNamespaceAndPath("citadel", "spawn_probability_modifier"))
            .add(ModificationPhase.POST_PROCESSING, context -> true, (selectionContext, modificationContext) -> {
                float probability = (float) (ServerConfig.chunkGenSpawnModifierVal) * ((SpawnSettingsContextImplAccessor) modificationContext.getSpawnSettings()).citadel$getSpawnSettings().getCreatureProbability();
                modificationContext.getSpawnSettings().setCreatureSpawnProbability(Mth.clamp(probability, 0f, 1f));
            });
    }
}
