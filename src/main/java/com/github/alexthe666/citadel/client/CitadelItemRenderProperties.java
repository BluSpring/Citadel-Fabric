package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.forge.extensions.IClientItemExtensions;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public class CitadelItemRenderProperties implements IClientItemExtensions {

    private final BlockEntityWithoutLevelRenderer renderer = new CitadelItemstackRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return renderer;
    }
}
