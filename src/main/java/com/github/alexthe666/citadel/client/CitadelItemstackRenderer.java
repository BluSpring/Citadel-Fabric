package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.item.components.CitadelComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CitadelItemstackRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation DEFAULT_ICON_TEXTURE = ResourceLocation.parse("citadel:textures/gui/book/icon_default.png");
    private static final Map<String, ResourceLocation> LOADED_ICONS = new HashMap<>();

    private static List<MobEffect> mobEffectList = null;

    public CitadelItemstackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        float partialTicks = (float) (Minecraft.getInstance().getFrameTimeNs() / TimeUtil.NANOSECONDS_PER_MILLISECOND);
        float ticksExisted = Util.getMillis() / 50F + partialTicks;
        int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
        if (stack.getItem() == Citadel.FANCY_ITEM.get()) {
            Random random = new Random();
            boolean animateAnyways = false;
            ItemStack toRender = null;

            if (stack.has(CitadelComponents.DISPLAY_ITEM_COMPONENT)) {
                toRender = stack.get(CitadelComponents.DISPLAY_ITEM_COMPONENT).display();
            }
            if (toRender == null) {
                animateAnyways = true;
                toRender = new ItemStack(Items.BARRIER);
            }
            matrixStack.pushPose();
            matrixStack.translate(0.5F, 0.5f, 0.5f);
            if(stack.has(CitadelComponents.DISPLAY_SHAKE_COMPONENT) && stack.get(CitadelComponents.DISPLAY_SHAKE_COMPONENT)) {
                matrixStack.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
            }
            if(animateAnyways || stack.has(CitadelComponents.DISPLAY_BOB_COMPONENT) && stack.get(CitadelComponents.DISPLAY_BOB_COMPONENT)){
                matrixStack.translate(0, 0.05F + 0.1F * Mth.sin(0.3F * ticksExisted), 0);
            }
            if(stack.has(CitadelComponents.DISPLAY_SPIN_COMPONENT) && stack.get(CitadelComponents.DISPLAY_SPIN_COMPONENT)){
                matrixStack.mulPose(Axis.YP.rotationDegrees(6 * ticksExisted));
            }
            if(animateAnyways || stack.has(CitadelComponents.DISPLAY_ZOOM_COMPONENT) && stack.get(CitadelComponents.DISPLAY_ZOOM_COMPONENT)) {
                float scale = (float) (1F + 0.15F * (Math.sin(ticksExisted * 0.3F) + 1F));
                matrixStack.scale(scale, scale, scale);
            }
            if(stack.has(CitadelComponents.DISPLAY_SCALE_COMPONENT) && stack.get(CitadelComponents.DISPLAY_SCALE_COMPONENT) != 1f){
                float scale = stack.get(CitadelComponents.DISPLAY_SCALE_COMPONENT);
                matrixStack.scale(scale, scale, scale);
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(toRender, transformType, combinedLight, combinedOverlay, matrixStack, buffer, null, id);
            matrixStack.popPose();
        }
        if (stack.getItem() == Citadel.EFFECT_ITEM.get()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
           // RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            MobEffect effect;
            if (stack.has(CitadelComponents.DISPLAY_EFFECT_COMPONENT)) {
                effect = BuiltInRegistries.MOB_EFFECT.get(stack.get(CitadelComponents.DISPLAY_EFFECT_COMPONENT));
            } else {
                if(mobEffectList == null){
                    mobEffectList = BuiltInRegistries.MOB_EFFECT.stream().toList();
                }
                int size = mobEffectList.size();
                int time = (int) (Util.getMillis() / 500);
                effect = mobEffectList.get(time % size);
                if (effect == null) {
                    effect = MobEffects.MOVEMENT_SPEED.value();
                }
            }
            if (effect == null) {
                effect = MobEffects.MOVEMENT_SPEED.value();
            }
            MobEffectTextureManager potionspriteuploader = Minecraft.getInstance().getMobEffectTextures();
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0.5F);
            TextureAtlasSprite sprite = potionspriteuploader.get(BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(BuiltInRegistries.MOB_EFFECT.getResourceKey(effect).orElseThrow()));
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, sprite.atlasLocation());
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            Matrix4f mx = matrixStack.last().pose();
            int br = 255;
            bufferbuilder.addVertex(mx, (float) 1, (float) 1, (float) 0).setUv(sprite.getU1(), sprite.getV0()).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 1, (float) 0).setUv(sprite.getU0(), sprite.getV0()).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 0, (float) 0).setUv(sprite.getU0(), sprite.getV1()).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(mx, (float) 1, (float) 0, (float) 0).setUv(sprite.getU1(), sprite.getV1()).setColor(br, br, br, 255).setLight(combinedLight);
            var data = bufferbuilder.build();
            RenderType.translucent().draw(data);
            data.close();
            matrixStack.popPose();
        }
        if (stack.getItem() == Citadel.ICON_ITEM.get()) {
            ResourceLocation texture = DEFAULT_ICON_TEXTURE;
            if (stack.has(CitadelComponents.ICON_LOCATION_COMPONENT)) {
                texture = stack.get(CitadelComponents.ICON_LOCATION_COMPONENT);
            }
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0.5F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, texture);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            Matrix4f mx = matrixStack.last().pose();
            int br = 255;
            bufferbuilder.addVertex(mx, (float) 1, (float) 1, (float) 0).setUv(1, 0).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 1, (float) 0).setUv(0, 0).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(mx, (float) 0, (float) 0, (float) 0).setUv(0, 1).setColor(br, br, br, 255).setLight(combinedLight);
            bufferbuilder.addVertex(mx, (float) 1, (float) 0, (float) 0).setUv(1, 1).setColor(br, br, br, 255).setLight(combinedLight);
            var data = bufferbuilder.build();
            RenderType.translucent().draw(data);
            data.close();
            matrixStack.popPose();
        }
    }


}
