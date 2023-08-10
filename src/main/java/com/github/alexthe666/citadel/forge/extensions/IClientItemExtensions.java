package com.github.alexthe666.citadel.forge.extensions;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public interface IClientItemExtensions {
    default BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return null;
    }
}
