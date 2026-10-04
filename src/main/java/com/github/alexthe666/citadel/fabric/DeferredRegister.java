package com.github.alexthe666.citadel.fabric;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DeferredRegister<T> {
    private final Registry<T> registry;
    private final String modId;

    private final List<T> entries = new ArrayList<>();

    public DeferredRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public <U extends T> Holder<T> registerHolder(String id, Supplier<U> supplier) {
        var value = Registry.registerForHolder(this.registry, ResourceLocation.fromNamespaceAndPath(modId, id), supplier.get());
        this.entries.add(value.value());
        return value;
    }

    public <U extends T> U register(String id, Supplier<U> supplier) {
        var value = Registry.register(this.registry, ResourceLocation.fromNamespaceAndPath(modId, id), supplier.get());
        this.entries.add(value);
        return value;
    }

    public List<T> getEntries() {
        return entries;
    }

    public void register() {}

    public static <T> DeferredRegister<T> create(Registry<T> registry, String modId) {
        return new DeferredRegister<>(registry, modId);
    }

    public static <T> DeferredRegister<T> create(ResourceKey<Registry<T>> registryKey, String modId) {
        return create(BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey), modId);
    }

    public static DeferredRegister.DataComponents createDataComponents(ResourceKey<Registry<DataComponentType<?>>> registryKey, String modId) {
        return new DataComponents(BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey), modId);
    }

    public static class DataComponents extends DeferredRegister<DataComponentType<?>> {
        public DataComponents(Registry<DataComponentType<?>> registry, String modId) {
            super(registry, modId);
        }

        public <T> DataComponentType<T> registerComponentType(String name, Consumer<DataComponentType.Builder<T>> builderConsumer) {
            return register(name, () -> {
                var builder = DataComponentType.<T>builder();
                builderConsumer.accept(builder);
                return builder.build();
            });
        }
    }
}
