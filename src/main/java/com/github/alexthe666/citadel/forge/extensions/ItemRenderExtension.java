package com.github.alexthe666.citadel.forge.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface ItemRenderExtension {
    Object getRenderPropertiesInternal();
    default String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type)
    {
        return null;
    }

    default void initializeClient(Consumer<IClientItemExtensions> consumer) {}
}
