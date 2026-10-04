package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;

import net.fabricmc.fabric.api.util.TriState;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @Mutable
    @Shadow
    @Final
    private String splash;

    @Unique private int citadel$splashTextColor = -1;

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    shift = At.Shift.BEFORE
            ))
    protected void citadel_preRenderSplashText(GuiGraphics guiGraphics, int width, Font font, int loadProgress, CallbackInfo ci) {
        guiGraphics.pose().pushPose();
        EventRenderSplashText.Pre event = new EventRenderSplashText.Pre(splash, guiGraphics, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks(), 16776960);
        event.sendEvent();

        if (event.getResult() == TriState.TRUE) {
            splash = event.getSplashText();
            citadel$splashTextColor = event.getSplashTextColor();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_postRenderSplashText(GuiGraphics guiGraphics, int width, Font font, int loadProgress, CallbackInfo ci) {
        EventRenderSplashText.Post event = new EventRenderSplashText.Post(splash, guiGraphics, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
        event.sendEvent();
        guiGraphics.pose().popPose();
    }

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V",
            at = @At(value = "CONSTANT", args = "intValue=16776960"))
    private int citadel_splashTextColor(int value) {
        return citadel$splashTextColor == -1 ? value : citadel$splashTextColor;
    }
}
