package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.shader.CitadelInternalShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;

public class ClientEvents {
    static {
        CoreShaderRegistrationCallback.EVENT.register(ClientEvents::registerShaders);
        BuiltinItemRendererRegistry.INSTANCE.register(Citadel.FANCY_ITEM, CitadelItemstackRenderer.INSTANCE::renderByItem);
        BuiltinItemRendererRegistry.INSTANCE.register(Citadel.EFFECT_ITEM, CitadelItemstackRenderer.INSTANCE::renderByItem);
        BuiltinItemRendererRegistry.INSTANCE.register(Citadel.ICON_ITEM, CitadelItemstackRenderer.INSTANCE::renderByItem);
    }

    public static void registerShaders(final CoreShaderRegistrationCallback.RegistrationContext context) {
        try {
            context.register(ResourceLocation.fromNamespaceAndPath("citadel", "rendertype_rainbow_aura"), DefaultVertexFormat.POSITION_TEX_COLOR, CitadelInternalShaders::setRenderTypeRainbowAura);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
