package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin implements ExpandedBiomeSource {

    @Shadow @Final @Mutable
    private Set<Holder<Biome>> possibleBiomes;
    private boolean expanded;
    private Map<ResourceKey<Biome>, Holder<Biome>> map = new HashMap<>();

    @Override
    public void setResourceKeyMap(Map<ResourceKey<Biome>, Holder<Biome>> map) {
        this.map = map;
    }

    @Override
    public Map<ResourceKey<Biome>, Holder<Biome>> getResourceKeyMap() {
        return map;
    }

    @Override
    public void expandBiomesWith(Set<Holder<Biome>> newGenBiomes) {
        if(!expanded){
            ImmutableSet.Builder<Holder<Biome>> builder = ImmutableSet.builder();
            builder.addAll(this.possibleBiomes);
            builder.addAll(newGenBiomes);
            this.possibleBiomes = new ObjectLinkedOpenHashSet<>(builder.build());
            expanded = true;
        }
    }
}
