package com.github.alexthe666.citadel.server.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.alexthe666.citadel.Citadel;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class VillageHouseManager {
    public static final List<Identifier> VILLAGE_REPLACEMENT_POOLS = List.of(
            Identifier.parse("minecraft:village/plains/houses"),
            Identifier.parse("minecraft:village/desert/houses"),
            Identifier.parse("minecraft:village/savanna/houses"),
            Identifier.parse("minecraft:village/snowy/houses"),
            Identifier.parse("minecraft:village/taiga/houses"));
    private static final List<Pair<Identifier, Consumer<StructureTemplatePool>>> REGISTRY = new ArrayList<>();

    public static void register(Identifier pool, Consumer<StructureTemplatePool> addToPool) {
        REGISTRY.add(new Pair<>(pool, addToPool));
        Citadel.LOGGER.debug("registered addition to pool: {}", pool);
    }

    public static StructureTemplatePool addToPool(StructureTemplatePool pool, StructurePoolElement element, int weight) {
        if (weight > 0) {
            if (pool != null) {
                ObjectArrayList<StructurePoolElement> templates = new ObjectArrayList<>(pool.templates);
                if (!templates.contains(element)) {
                    for (int i = 0; i < weight; i++) {
                        templates.add(element);
                    }
                    List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList(pool.rawTemplates);
                    rawTemplates.add(new Pair<>(element, weight));
                    pool.templates = templates;
                    pool.rawTemplates = rawTemplates;
                    Citadel.LOGGER.info("Added to village structure pool");
                }
            }
        }
        return pool;
    }

    public static void addAllHouses(RegistryAccess registryAccess) {
        try {
            for (Identifier villagePool : VILLAGE_REPLACEMENT_POOLS) {
                StructureTemplatePool pool = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL).getOptional(villagePool).orElse(null);
                if (pool != null) {
                    for (Pair<Identifier, Consumer<StructureTemplatePool>> pair : REGISTRY) {
                        if (villagePool.equals(pair.getFirst())) {
                            pair.getSecond().accept(pool);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Citadel.LOGGER.error("Could not add village houses!");
            e.printStackTrace();
        }
    }
}
