package com.github.alexthe666.citadel.item.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class CitadelComponents {
    public static final DataComponentType<DisplayItemComponent> DISPLAY_ITEM_COMPONENT = register("display_item", builder ->
        builder.persistent(DisplayItemComponent.CODEC).networkSynchronized(DisplayItemComponent.STREAM_CODEC)
    );

    public static final DataComponentType<Boolean> DISPLAY_SHAKE_COMPONENT = register("display_shake", builder ->
        builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );

    public static final DataComponentType<Boolean> DISPLAY_BOB_COMPONENT = register("display_bob", builder ->
        builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );

    public static final DataComponentType<Boolean> DISPLAY_SPIN_COMPONENT = register("display_spin", builder ->
        builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );

    public static final DataComponentType<Boolean> DISPLAY_ZOOM_COMPONENT = register("display_zoom", builder ->
        builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
    );

    public static final DataComponentType<Float> DISPLAY_SCALE_COMPONENT = register("display_scale", builder ->
        builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT)
    );

    public static final DataComponentType<ResourceLocation> DISPLAY_EFFECT_COMPONENT = register("display_effect", builder ->
        builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
    );

    public static final DataComponentType<ResourceLocation> ICON_LOCATION_COMPONENT = register("icon_location", builder ->
        builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC)
    );

    public static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath("citadel", id), builder.apply(DataComponentType.builder()).build());
    }

    public static void init() {}
}
