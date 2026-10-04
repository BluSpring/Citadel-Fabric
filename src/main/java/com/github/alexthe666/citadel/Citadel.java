package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.fabric.DeferredRegister;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCitadelDebug;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.item.component.CustomRenderDisplay;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.generation.SpawnProbabilityModifier;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.message.*;
import com.github.alexthe666.citadel.web.WebHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import io.github.fabricators_of_create.porting_lib.config.ConfigRegistry;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class Citadel implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static ServerProxy PROXY = unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static List<String> PATREONS = new ArrayList<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "citadel");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "citadel");
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "citadel");
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "citadel");

    public static final ItemCitadelDebug DEBUG_ITEM = ITEMS.register("debug", () -> new ItemCitadelDebug(new Item.Properties()));
    public static final ItemCitadelBook CITADEL_BOOK = ITEMS.register("citadel_book", () -> new ItemCitadelBook(new Item.Properties().stacksTo(1)));
    public static final ItemCustomRender EFFECT_ITEM = ITEMS.register("effect_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final ItemCustomRender FANCY_ITEM = ITEMS.register("fancy_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final ItemCustomRender ICON_ITEM = ITEMS.register("icon_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));

    public static final DataComponentType<CustomRenderDisplay> CUSTOM_RENDER_DISPLAY = DATA_COMPONENTS.registerComponentType("custom_render_display", builder -> builder.persistent(CustomRenderDisplay.CODEC));
    public static final DataComponentType<ResourceLocation> ICON_LOCATION = DATA_COMPONENTS.registerComponentType("icon_location", builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC));
    public static final DataComponentType<ResourceKey<MobEffect>> DISPLAY_EFFECT = DATA_COMPONENTS.registerComponentType("display_effect", builder -> builder.persistent(ResourceKey.codec(Registries.MOB_EFFECT)).networkSynchronized(ResourceKey.streamCodec(Registries.MOB_EFFECT)));

    public static final Block LECTERN = BLOCKS.register("lectern", () -> new CitadelLecternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)));

    public static final BlockEntityType<CitadelLecternBlockEntity> LECTERN_BE = BLOCK_ENTITIES.register("lectern", () -> BlockEntityType.Builder.of(CitadelLecternBlockEntity::new, LECTERN).build(null));

    @Override
    public void onInitialize() {
        setup();
        ITEMS.register();
        BLOCKS.register();
        BLOCK_ENTITIES.register();
        DATA_COMPONENTS.register();
        registerPayloads();

        SpawnProbabilityModifier.setup();
        ConfigRegistry.registerConfig("citadel", ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        new CitadelEvents();
        ModConfigEvent.Reloading.EVENT.register(Citadel::onModConfigEvent);

        ServerLifecycleEvents.SERVER_STARTING.register(Citadel::onServerAboutToStart);
    }

    public static void setup() {
        {
            PROXY.onPreInit();
            LecternBooks.init();
            BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/Alex-the-666/Citadel/master/src/main/resources/assets/citadel/patreon.txt", "assets/citadel/patreon.txt");
            if (urlContents != null) {
                try {
                    String line;
                    while ((line = urlContents.readLine()) != null) {
                        PATREONS.add(line);
                    }
                } catch (IOException e) {
                    LOGGER.warn("Failed to load patreon contributor perks");
                }
            } else LOGGER.warn("Failed to load patreon contributor perks");
        }
    }

    public static void onModConfigEvent(final ModConfigEvent.Reloading event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        ServerConfig.skipWarnings = ConfigHolder.SERVER.skipDatapackWarnings.get();
        if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
            ServerConfig.citadelEntityTrack = ConfigHolder.SERVER.citadelEntityTracker.get();
            ServerConfig.chunkGenSpawnModifierVal = ConfigHolder.SERVER.chunkGenSpawnModifier.get();
            ServerConfig.aprilFools = ConfigHolder.SERVER.aprilFoolsContent.get();
            //citadelTestBiomeData = SpawnBiomeConfig.create(ResourceLocation.parse("citadel:config_biome"), CitadelBiomeDefinitions.TERRALITH_TEST);
        }
    }

    public static void registerPayloads() {
        // PropertiesMessage is bidirectional - used by both client (GUI) and server (entity utils)
        PayloadTypeRegistry.playC2S().register(PropertiesMessage.TYPE, PropertiesMessage.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PropertiesMessage.TYPE, PropertiesMessage::handle);
        PayloadTypeRegistry.playS2C().register(PropertiesMessage.TYPE, PropertiesMessage.CODEC);
        // AnimationMessage is sent from server to all clients via sendToAllPlayers
        PayloadTypeRegistry.playS2C().register(AnimationMessage.TYPE, AnimationMessage.CODEC);
        // DanceJukeboxMessage is sent from client to server via sendToServer
        PayloadTypeRegistry.playC2S().register(DanceJukeboxMessage.TYPE, DanceJukeboxMessage.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DanceJukeboxMessage.TYPE, DanceJukeboxMessage::handle);
        // SyncePathMessage is sent from server to specific player via sendToPlayer
        PayloadTypeRegistry.playS2C().register(SyncePathMessage.TYPE, SyncePathMessage.CODEC);
        // SyncPathReachedMessage is sent from server to specific player via sendToPlayer
        PayloadTypeRegistry.playS2C().register(SyncPathReachedMessage.TYPE, SyncPathReachedMessage.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncClientTickRateMessage.TYPE, SyncClientTickRateMessage.CODEC);
    }

    public static void onServerAboutToStart(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        VillageHouseManager.addAllHouses(registryAccess);
    }

    private static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> clientTarget.get().get();
            case SERVER -> serverTarget.get().get();
        };
    }
}