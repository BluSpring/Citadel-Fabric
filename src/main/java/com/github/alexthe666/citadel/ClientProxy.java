package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.event.*;
import com.github.alexthe666.citadel.client.game.Tetris;
import com.github.alexthe666.citadel.client.gui.GuiCitadelCapesConfig;
import com.github.alexthe666.citadel.client.render.CitadelLecternRenderer;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.rewards.SpaceStationPatreonRenderer;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemWithHoverAnimation;
import com.github.alexthe666.citadel.mixin.BackupConfirmScreenAccessor;
import com.github.alexthe666.citadel.mixin.ConfirmScreenAccessor;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.github.alexthe666.citadel.server.message.CitadelMessages;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientProxy extends ServerProxy {
    public static TabulaModel CITADEL_MODEL;
    public static boolean hideFollower = false;
    private Map<ItemStack, Float> prevMouseOverProgresses = new HashMap<>();

    private Map<ItemStack, Float> mouseOverProgresses = new HashMap<>();
    private ItemStack lastHoveredItem = null;
    private Tetris aprilFoolsTetrisGame = null;

    public ClientProxy() {
        super();

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            onOpenGui(screen);
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            screenOpen(screen);

            if (screen instanceof TitleScreen) {
                ScreenEvents.afterRender(screen).register((screen1, drawContext, mouseX, mouseY, tickDelta) -> {
                    screenRender(screen1, drawContext, tickDelta);
                });

                ScreenKeyboardEvents.allowKeyPress(screen).register((screen1, key, scancode, modifiers) -> {
                    return onKeyPressed(key);
                });
            }
        });

        EventRenderSplashText.PRE.register(this::renderSplashTextBefore);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientTick();
        });

        ClientTooltipEvent.RENDER_MODIFY_COLOR.register((graphics, x, y, context) -> {
            var stack = ClientTooltipEvent.additionalContexts().getItem();

            if (stack == null || stack.isEmpty())
                return;

            renderTooltipColor(stack);
        });
    }

    public void onClientInit() {
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BlockEntityRenderers.register(Citadel.LECTERN_BE.get(), CitadelLecternRenderer::new);
        CitadelPatreonRenderer.register("citadel", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station"), new int[]{}));
        CitadelPatreonRenderer.register("citadel_red", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station_red"), new int[]{0XB25048, 0X9D4540, 0X7A3631, 0X71302A}));
        CitadelPatreonRenderer.register("citadel_gray", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station_gray"), new int[]{0XA0A0A0, 0X888888, 0X646464, 0X575757}));

        ClientPlayNetworking.registerGlobalReceiver(CitadelMessages.ANIMATION_TYPE, (payload, context) -> payload.handleClient());
        ClientPlayNetworking.registerGlobalReceiver(CitadelMessages.DANCE_JUKEBOX_TYPE, (payload, context) -> payload.handleClient());
        ClientPlayNetworking.registerGlobalReceiver(CitadelMessages.PROPERTIES_TYPE, (payload, context) -> payload.handleClient());
        ClientPlayNetworking.registerGlobalReceiver(CitadelMessages.SYNC_CLIENT_TICK_RATE_TYPE, (payload, context) -> payload.handleClient());
    }

    public void screenOpen(Screen screen) {
        if (screen instanceof SkinCustomizationScreen && Minecraft.getInstance().player != null) {
           try{
               String username = Minecraft.getInstance().player.getName().getString();
               int height = -20;
               if (Citadel.PATREONS.contains(username)) {
                   Button button1 = Button.builder(Component.translatable("citadel.gui.patreon_rewards_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelPatreonConfig(screen, Minecraft.getInstance().options));
                   }).size(200, 20).pos(screen.width / 2 - 100, screen.height / 6 + 150 + height).build();
                   ((ScreenAccess) screen).addRenderableWidget(button1);
                   height += 25;
               }
               if (!CitadelCapes.getCapesFor(Minecraft.getInstance().player.getUUID()).isEmpty()) {
                   Button button2 = Button.builder(Component.translatable("citadel.gui.capes_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                       Minecraft.getInstance().setScreen(new GuiCitadelCapesConfig(screen, Minecraft.getInstance().options));
                   }).size(200, 20).pos(screen.width / 2 - 100, screen.height / 6 + 150 + height).build();
                   ((ScreenAccess) screen).addRenderableWidget(button2);
                   height += 25;
               }
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }

    public void screenRender(Screen screen, GuiGraphics guiGraphics, float partialTick) {
        if(screen instanceof TitleScreen && CitadelConstants.isAprilFools()) {
            if(aprilFoolsTetrisGame == null){
                aprilFoolsTetrisGame = new Tetris();
            }else{
                aprilFoolsTetrisGame.render((TitleScreen) screen, guiGraphics, partialTick);
            }
        }
    }

    public void playerRender(PoseStack matrixStackIn, AbstractClientPlayer player, MultiBufferSource bufferSource, int light, float partialTick) {
        String username = player.getName().getString();
        if (!player.isModelPartShown(PlayerModelPart.CAPE)) {
            return;
        }
        if (Citadel.PATREONS.contains(username)) {
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
            if (!rendererName.equals("none") && !hideFollower) {
                CitadelPatreonRenderer renderer = CitadelPatreonRenderer.get(rendererName);
                if (renderer != null) {
                    float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
                    float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
                    float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
                    renderer.render(matrixStackIn, bufferSource, light, partialTick, player, distance, speed, height);
                }
            }
        }
    }

    public void onOpenGui(Screen screen) {
        if (ServerConfig.skipWarnings) {
            try{
                if (screen instanceof BackupConfirmScreen) {
                    BackupConfirmScreen confirmBackupScreen = (BackupConfirmScreen) screen;
                    String name = "";
                    MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");

                    if (confirmBackupScreen.getTitle().equals(title)) {
                        ((BackupConfirmScreenAccessor) confirmBackupScreen).getOnProceed().proceed(false, true);
                    }
                }
                if (screen instanceof ConfirmScreen) {
                    ConfirmScreen confirmScreen = (ConfirmScreen) screen;
                    MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");
                    String name = "";
                    if (confirmScreen.getTitle().equals(title)) {
                        ((ConfirmScreenAccessor) confirmScreen).getCallback().accept(true);
                    }
                }
            }catch (Exception e){
                Citadel.LOGGER.warn("Citadel couldn't skip world loadings");
                e.printStackTrace();
            }
        }
    }

    public EventResult renderSplashTextBefore(EventRenderSplashText.Pre event) {
        if(CitadelConstants.isAprilFools() && aprilFoolsTetrisGame != null){
            float hue = (System.currentTimeMillis() % 6000) / 6000f;
            event.getGuiGraphics().pose().mulPose(Axis.ZP.rotationDegrees((float)Math.sin(hue * Math.PI) * 360));
            if(!aprilFoolsTetrisGame.isStarted()){
                event.setSplashText("Psst... press 'T' ;)");
            }else{
                event.setSplashText("");
            }
            int rainbow = Color.HSBtoRGB(hue, 0.6f, 1);
            event.setSplashTextColor(rainbow);
            return EventResult.interruptTrue();
        }

        return EventResult.pass();
    }

    public boolean onKeyPressed(int keyCode) {
        if(Minecraft.getInstance().screen instanceof TitleScreen && aprilFoolsTetrisGame != null && aprilFoolsTetrisGame.isStarted()){
            if(keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_RIGHT || keyCode == InputConstants.KEY_DOWN || keyCode == InputConstants.KEY_UP){
                return false;
            }
        }

        return true;
    }

    public void clientTick() {
        if(!isGamePaused()){
            ClientTickRateTracker.getForClient(Minecraft.getInstance()).masterTick();
            tickMouseOverAnimations();
        }
        if(!isGamePaused() && CitadelConstants.isAprilFools()) {
            if(aprilFoolsTetrisGame != null){
                if(Minecraft.getInstance().screen instanceof TitleScreen){
                    aprilFoolsTetrisGame.tick();
                }else{
                    aprilFoolsTetrisGame.reset();
                }
            }
        }
    }

    private void tickMouseOverAnimations() {
        prevMouseOverProgresses.putAll(mouseOverProgresses);
        if (lastHoveredItem != null) {
            float prev = mouseOverProgresses.getOrDefault(lastHoveredItem, 0F);
            float maxTime = 5F;
            if(lastHoveredItem.getItem() instanceof ItemWithHoverAnimation hoverOver){
                maxTime = hoverOver.getMaxHoverOverTime(lastHoveredItem);
            }
            if (prev < maxTime) {
                mouseOverProgresses.put(lastHoveredItem, prev + 1);
            }
        }

        if (!mouseOverProgresses.isEmpty()) {
            Iterator<Map.Entry<ItemStack, Float>> it = mouseOverProgresses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ItemStack, Float> next = it.next();
                float progress = next.getValue();
                if (lastHoveredItem == null || next.getKey() != lastHoveredItem) {
                    if (progress == 0) {
                        it.remove();
                    } else {
                        next.setValue(progress - 1);
                    }
                }
            }
        }
        lastHoveredItem = null;
    }

    public void renderTooltipColor(ItemStack stack) {
        if (stack.getItem() instanceof ItemWithHoverAnimation hoverOver && hoverOver.canHoverOver(stack)) {
            lastHoveredItem = stack;
        } else {
            lastHoveredItem = null;
        }
    }

    @Override
    public float getMouseOverProgress(ItemStack itemStack){
        float prev = prevMouseOverProgresses.getOrDefault(itemStack, 0F);
        float current = mouseOverProgresses.getOrDefault(itemStack, 0F);
        float lerped = prev + (current - prev) * (float) (Minecraft.getInstance().getFrameTimeNs() / TimeUtil.NANOSECONDS_PER_MILLISECOND);
        float maxTime = 5F;
        if(itemStack.getItem() instanceof ItemWithHoverAnimation hoverOver){
            maxTime = hoverOver.getMaxHoverOverTime(itemStack);
        }
        return lerped / maxTime;
    }

        @Override
    public void handleAnimationPacket(int entityId, int index) {
        if (Minecraft.getInstance().level != null) {
            IAnimatedEntity entity = (IAnimatedEntity) Minecraft.getInstance().level.getEntity(entityId);
            if (entity != null) {
                if (index == -1) {
                    entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                } else {
                    entity.setAnimation(entity.getAnimations()[index]);
                }
                entity.setAnimationTick(0);
            }
        }
    }

    @Override
    public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
        if(compound == null || Minecraft.getInstance().level == null){
            return;
        }
        Entity entity = Minecraft.getInstance().level.getEntity(entityID);
        if ((propertyID.equals("CitadelPatreonConfig") || propertyID.equals("CitadelTagUpdate")) && entity instanceof LivingEntity) {
            CitadelEntityData.setCitadelTag((LivingEntity) entity, compound);
        }
    }


    @Override
    public void handleClientTickRatePacket(CompoundTag compound) {
        ClientTickRateTracker.getForClient(Minecraft.getInstance()).syncFromServer(compound);
    }

    @Override
    public Object getISTERProperties() {
        //return new CitadelItemRenderProperties();
        throw new IllegalStateException("We're not supposed to be here!");
    }

    @Override
    public void openBookGUI(ItemStack book) {
        Minecraft.getInstance().setScreen(new GuiCitadelBook(book));
    }

    public boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public boolean canEntityTickClient(Level level, Entity entity) {
        ClientTickRateTracker tracker = ClientTickRateTracker.getForClient(Minecraft.getInstance());
        if(tracker.isTickingHandled(entity)){
            return false;
        }else if(!tracker.hasNormalTickRate(entity)){
            EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
            if(EventChangeEntityTickRate.EVENT.invoker().onChangeEntityTickRate(event).isFalse()){
                return true;
            }else{
                tracker.addTickBlockedEntity(entity);
                return false;
            }
        }
        return true;
    }
}
