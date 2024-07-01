package com.github.alexthe666.citadel.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record DisplayItemComponent(
    ItemStack display
) {
    public static final Codec<DisplayItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("display")
                .forGetter(DisplayItemComponent::display)
        )
            .apply(instance, DisplayItemComponent::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DisplayItemComponent> STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, DisplayItemComponent::display, DisplayItemComponent::new);
}
