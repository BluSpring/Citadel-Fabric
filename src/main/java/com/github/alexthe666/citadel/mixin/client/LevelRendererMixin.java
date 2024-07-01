package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "initOutline()V",
            at = @At("TAIL"))
    private void ac_initOutline(CallbackInfo ci) {
        PostEffectRegistry.onInitializeOutline();
    }

    @Inject(method = "resize(II)V",
            at = @At("TAIL"))
    private void ac_resize(int x, int y, CallbackInfo ci) {
        PostEffectRegistry.resize(x, y);
    }

    @Inject(method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderBuffers;bufferSource()Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;",
                    shift = At.Shift.BEFORE
            ))
    private void ac_renderLevel_beforeEntities(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        PostEffectRegistry.copyDepth(this.minecraft.getMainRenderTarget());
    }

    @Inject(method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V",
                    shift = At.Shift.BEFORE
            ))
    private void ac_renderLevel_process(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci, @Local(ordinal = 0) float f) {
        PostEffectRegistry.processEffects(this.minecraft.getMainRenderTarget(), f);
    }

    @Inject(method = "renderLevel",
            at = @At(
                    value = "TAIL"
            ))
    private void ac_renderLevel_end(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f frustumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        PostEffectRegistry.blitEffects();
    }




    @Redirect(
            method = "renderLevel",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I")
    )
    private int citadel_getTeamColor(Entity entity) {
        EventGetOutlineColor event = new EventGetOutlineColor(entity, entity.getTeamColor());
        int color = entity.getTeamColor();
        if (EventGetOutlineColor.EVENT.invoker().onGetOutlineColor(event).isTrue()) {
            color = event.getColor();
        }
        return color;
    }

    @Redirect(
            method = "renderSky",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getTimeOfDay(F)F"),
            expect = 2
    )
    private float citadel_getTimeOfDay(ClientLevel instance, float partialTicks) {
        //default implementation does not lerp the time of day
        float lerpBy = Citadel.PROXY.isGamePaused() ? 0F : partialTicks;
        float lerpedDayTime = (instance.dimensionType().fixedTime().orElse(instance.dayTime()) + lerpBy) / 24000.0F;
        double d0 = Mth.frac((double)lerpedDayTime - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float)(d0 * 2.0D + d1) / 3.0F;
    }
}