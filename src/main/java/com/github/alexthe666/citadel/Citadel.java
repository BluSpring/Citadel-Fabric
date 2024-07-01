package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.config.biome.CitadelBiomeDefinitions;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCitadelDebug;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.item.components.CitadelComponents;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.message.*;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import com.github.alexthe666.citadel.web.WebHelper;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeModConfigEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.*;

public class Citadel implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final ResourceLocation PACKET_NETWORK_NAME = ResourceLocation.parse("citadel:main_channel");
    /*public static final SimpleChannel NETWORK_WRAPPER = NetworkRegistry.ChannelBuilder
            .named(PACKET_NETWORK_NAME)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();*/
    public static ServerProxy PROXY = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? new ClientProxy() : new ServerProxy();
    public static List<String> PATREONS = new ArrayList<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create("citadel", Registries.ITEM);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create("citadel", Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create("citadel", Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<Item> DEBUG_ITEM = ITEMS.register("debug", () -> new ItemCitadelDebug(new Item.Properties()));
    public static final RegistrySupplier<Item> CITADEL_BOOK = ITEMS.register("citadel_book", () -> new ItemCitadelBook(new Item.Properties().stacksTo(1)));
    public static final RegistrySupplier<Item> EFFECT_ITEM = ITEMS.register("effect_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final RegistrySupplier<Item> FANCY_ITEM = ITEMS.register("fancy_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final RegistrySupplier<Item> ICON_ITEM = ITEMS.register("icon_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));

    public static final RegistrySupplier<Block> LECTERN = BLOCKS.register("lectern", () -> new CitadelLecternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)));

    public static final RegistrySupplier<BlockEntityType<CitadelLecternBlockEntity>> LECTERN_BE = BLOCK_ENTITIES.register("lectern", () -> BlockEntityType.Builder.of(CitadelLecternBlockEntity::new, LECTERN.get()).build(null));

    private static MinecraftServer server;

    public static MinecraftServer getCurrentServer() {
        return server;
    }

    @Override
    public void onInitialize() {
        ForgeModConfigEvents.loading("citadel").register(this::onModConfigEvent);
        ForgeModConfigEvents.reloading("citadel").register(this::onModConfigEvent);
        ForgeModConfigEvents.unloading("citadel").register(this::onModConfigEvent);
        ITEMS.register();
        BLOCKS.register();
        BLOCK_ENTITIES.register();
        // TODO: port ForgeBiomeModifiers? not needed atm tho
        //final DeferredRegister<Codec<? extends BiomeModifier>> serializers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, "citadel");
        //serializers.register();
        //serializers.register("mob_spawn_probability", SpawnProbabilityModifier::makeCodec);
        //MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.register(PROXY);
        //final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        //modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        ForgeConfigRegistry.INSTANCE.register("citadel", ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        new CitadelEvents();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Citadel.server = server;
            onServerAboutToStart(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            Citadel.server = null;
        });

        CitadelComponents.init();

        this.setup();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.doClientStuff();
        }
    }

    public static <MSG extends CitadelPacket> void sendMSGToServer(MSG message) {
        ClientPlayNetworking.send(message);
    }

    public static <MSG extends CitadelPacket> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG extends CitadelPacket> void sendNonLocal(MSG msg, ServerPlayer player) {
        ServerPlayNetworking.send(player, msg);
    }

    private void setup() {
        PROXY.onPreInit();
        LecternBooks.init();

        PayloadTypeRegistry.playC2S().register(CitadelMessages.ANIMATION_TYPE, CitadelMessages.ANIMATION_CODEC);
        PayloadTypeRegistry.playC2S().register(CitadelMessages.DANCE_JUKEBOX_TYPE, CitadelMessages.DANCE_JUKEBOX_CODEC);
        PayloadTypeRegistry.playC2S().register(CitadelMessages.PROPERTIES_TYPE, CitadelMessages.PROPERTIES_CODEC);
        PayloadTypeRegistry.playC2S().register(CitadelMessages.SYNC_CLIENT_TICK_RATE_TYPE, CitadelMessages.SYNC_CLIENT_TICK_RATE_CODEC);
        PayloadTypeRegistry.playS2C().register(CitadelMessages.ANIMATION_TYPE, CitadelMessages.ANIMATION_CODEC);
        PayloadTypeRegistry.playS2C().register(CitadelMessages.DANCE_JUKEBOX_TYPE, CitadelMessages.DANCE_JUKEBOX_CODEC);
        PayloadTypeRegistry.playS2C().register(CitadelMessages.PROPERTIES_TYPE, CitadelMessages.PROPERTIES_CODEC);
        PayloadTypeRegistry.playS2C().register(CitadelMessages.SYNC_CLIENT_TICK_RATE_TYPE, CitadelMessages.SYNC_CLIENT_TICK_RATE_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(CitadelMessages.ANIMATION_TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(CitadelMessages.DANCE_JUKEBOX_TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(CitadelMessages.PROPERTIES_TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(CitadelMessages.SYNC_CLIENT_TICK_RATE_TYPE, (payload, context) -> payload.handleServer(context.player()));
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

    public void onModConfigEvent(final ModConfig config) {
        // Rebake the configs when they change
        ServerConfig.skipWarnings = ConfigHolder.SERVER.skipDatapackWarnings.get();
        if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
            ServerConfig.citadelEntityTrack = ConfigHolder.SERVER.citadelEntityTracker.get();
            ServerConfig.chunkGenSpawnModifierVal = ConfigHolder.SERVER.chunkGenSpawnModifier.get();
            ServerConfig.aprilFools = ConfigHolder.SERVER.aprilFoolsContent.get();
            //citadelTestBiomeData = SpawnBiomeConfig.create(new ResourceLocation("citadel:config_biome"), CitadelBiomeDefinitions.TERRALITH_TEST);
        }
    }



    private void doClientStuff() {
        PROXY.onClientInit();
    }

    public void onServerAboutToStart(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        VillageHouseManager.addAllHouses(registryAccess);
        Registry<Biome> allBiomes = registryAccess.registryOrThrow(Registries.BIOME);
        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = new HashMap<>();
        for(ResourceKey<Biome> biomeResourceKey : allBiomes.registryKeySet()){
            Optional<Holder.Reference<Biome>> holderOptional = allBiomes.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
        }
        for (ResourceKey<LevelStem> levelStemResourceKey : levelStems.registryKeySet()) {
            Optional<Holder.Reference<LevelStem>> holderOptional = levelStems.getHolder(levelStemResourceKey);
            if(holderOptional.isPresent() && holderOptional.get().value().generator().getBiomeSource() instanceof ExpandedBiomeSource expandedBiomeSource){
                expandedBiomeSource.setResourceKeyMap(biomeMap);
                Set<Holder<Biome>> biomeHolders = ExpandedBiomes.buildBiomeList(registryAccess, levelStemResourceKey);
                expandedBiomeSource.expandBiomesWith(biomeHolders);
            }
        }
    }

}