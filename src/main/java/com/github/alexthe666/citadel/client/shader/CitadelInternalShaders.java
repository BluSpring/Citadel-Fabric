package com.github.alexthe666.citadel.client.shader;

import net.minecraft.client.renderer.ShaderInstance;

import org.jspecify.annotations.Nullable;

public class CitadelInternalShaders {
    private static ShaderInstance renderTypeRainbowAura;

    @Nullable
    public static ShaderInstance getRenderTypeRainbowAura() {
        return renderTypeRainbowAura;
    }

    public static void setRenderTypeRainbowAura(ShaderInstance instance) {
        renderTypeRainbowAura = instance;
    }
}
