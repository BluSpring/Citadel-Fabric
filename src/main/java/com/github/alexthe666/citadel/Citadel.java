package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCitadelDebug;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.generation.SpawnProbabilityModifier;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.message.AnimationMessage;
import com.github.alexthe666.citadel.server.message.DanceJukeboxMessage;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.citadel.server.message.SyncClientTickRateMessage;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import com.github.alexthe666.citadel.web.WebHelper;
import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.bluspring.forgebiomemodifiers.worldgen.BiomeModifier;
import xyz.bluspring.forgebiomemodifiers.worldgen.BiomeModifiers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


public class Citadel implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation("citadel:main_channel");
    public static final SimpleChannel NETWORK_WRAPPER = new SimpleChannel(PACKET_NETWORK_NAME);
    public static ServerProxy PROXY = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? new ClientProxy() : new ServerProxy();
    public static List<String> PATREONS = new ArrayList<>();

    public static final LazyRegistrar<Item> ITEMS = LazyRegistrar.create(Registry.ITEM, "citadel");
    public static final LazyRegistrar<Block> BLOCKS = LazyRegistrar.create(Registry.BLOCK, "citadel");
    public static final LazyRegistrar<BlockEntityType<?>> BLOCK_ENTITIES = LazyRegistrar.create(Registry.BLOCK_ENTITY_TYPE, "citadel");

    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug", () -> new ItemCitadelDebug(new Item.Properties()));
    public static final RegistryObject<Item> CITADEL_BOOK = ITEMS.register("citadel_book", () -> new ItemCitadelBook(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> EFFECT_ITEM = ITEMS.register("effect_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FANCY_ITEM = ITEMS.register("fancy_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ICON_ITEM = ITEMS.register("icon_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Block> LECTERN = BLOCKS.register("lectern", () -> new CitadelLecternBlock(BlockBehaviour.Properties.copy(Blocks.LECTERN)));

    public static final RegistryObject<BlockEntityType<CitadelLecternBlockEntity>> LECTERN_BE = BLOCK_ENTITIES.register("lectern", () -> BlockEntityType.Builder.of(CitadelLecternBlockEntity::new, LECTERN.get()).build(null));


    @Override
    public void onInitialize() {
        this.setup();
        this.enqueueIMC();
        this.processIMC();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            this.doClientStuff();
        }

        ModConfigEvents.loading("citadel").register(this::onModConfigEvent);

        ITEMS.register();
        BLOCKS.register();
        BLOCK_ENTITIES.register();
        final LazyRegistrar<Codec<? extends BiomeModifier>> serializers = LazyRegistrar.create(BiomeModifiers.BIOME_MODIFIER_SERIALIZER_KEY, "citadel");
        serializers.register();
        serializers.register("mob_spawn_probability", SpawnProbabilityModifier::makeCodec);

        PROXY.initEvents();
        ModLoadingContext.registerConfig("citadel", ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        new CitadelEvents();
    }

    public static <MSG extends C2SPacket> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }

    public static <MSG extends S2CPacket> void sendMSGToAll(MSG message) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG extends S2CPacket> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendToClient(msg, player);
    }

    private void setup() {
        PROXY.onPreInit();
        LecternBooks.init();
        int packetsRegistered = 0;

        NETWORK_WRAPPER.registerS2CPacket(PropertiesMessage.class, packetsRegistered++, PropertiesMessage::read);
        NETWORK_WRAPPER.registerC2SPacket(PropertiesMessage.class, packetsRegistered++, PropertiesMessage::read);

        NETWORK_WRAPPER.registerS2CPacket(AnimationMessage.class, packetsRegistered++, AnimationMessage::read);
        NETWORK_WRAPPER.registerC2SPacket(AnimationMessage.class, packetsRegistered++, AnimationMessage::read);

        NETWORK_WRAPPER.registerS2CPacket(SyncClientTickRateMessage.class, packetsRegistered++, SyncClientTickRateMessage::read);
        NETWORK_WRAPPER.registerC2SPacket(SyncClientTickRateMessage.class, packetsRegistered++, SyncClientTickRateMessage::read);

        NETWORK_WRAPPER.registerS2CPacket(DanceJukeboxMessage.class, packetsRegistered++, DanceJukeboxMessage::read);
        NETWORK_WRAPPER.registerC2SPacket(DanceJukeboxMessage.class, packetsRegistered++, DanceJukeboxMessage::read);

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

        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerAboutToStart);
    }

    public void onModConfigEvent(ModConfig config) {
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
        ClientLifecycleEvents.CLIENT_STARTED.register((mc) -> {
            PROXY.onClientInit();
            NETWORK_WRAPPER.initClientListener();
        });
    }

    private void enqueueIMC() {

    }

    private void processIMC() {

    }

    public void onServerAboutToStart(MinecraftServer server) {
        NETWORK_WRAPPER.initServerListener();

        RegistryAccess registryAccess = server.registryAccess();
        VillageHouseManager.addAllHouses(registryAccess);
        Registry<Biome> allBiomes = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = new HashMap<>();
        for(ResourceKey<Biome> biomeResourceKey : allBiomes.registryKeySet()){
            Optional<Holder<Biome>> holderOptional = allBiomes.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
        }
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : server.getWorldData().worldGenSettings().dimensions().entrySet()) {
            if(entry.getValue().generator().getBiomeSource() instanceof ExpandedBiomeSource expandedBiomeSource){
                expandedBiomeSource.setResourceKeyMap(biomeMap);
                Set<Holder<Biome>> biomeHolders = ExpandedBiomes.buildBiomeList(registryAccess, entry.getKey());
                expandedBiomeSource.expandBiomesWith(biomeHolders);
            }
        }
    }

}