package com.github.alexthe666.citadel.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.biome.MobSpawnSettings;

@Mixin(targets = "net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$SpawnSettingsContextImpl")
public interface SpawnSettingsContextImplAccessor {
    @Accessor("spawnSettings")
    MobSpawnSettings citadel$getSpawnSettings();
}
