package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.server.entity.IDancesToJukebox;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.github.alexthe666.citadel.server.tick.ServerTickRateTracker;
import com.github.alexthe666.citadel.server.world.CitadelServerData;
import com.github.alexthe666.citadel.server.world.ModifiableTickRateServer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ServerProxy {


    public ServerProxy() {
    }

    public void initEvents() {
    }

    public void onPreInit() {
    }

    public void handleAnimationPacket(int entityId, int index) {

    }

    public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
    }

    public void handleClientTickRatePacket(CompoundTag compound) {
    }

    public void handleJukeboxPacket(Level level, int entityId, BlockPos jukeBox, boolean dancing) {
        Entity entity = level.getEntity(entityId);
        if (entity instanceof IDancesToJukebox dancer) {
            dancer.setDancing(dancing);
            dancer.setJukeboxPos(dancing ? jukeBox : null);
        }
    }


    public void openBookGUI(ItemStack book) {
    }

    public Object getISTERProperties() {
        return null;
    }

    public void onClientInit() {
        var phase = new ResourceLocation("citadel", "late");
        ServerTickEvents.START_SERVER_TICK.addPhaseOrdering(Event.DEFAULT_PHASE, phase);
        ServerTickEvents.START_SERVER_TICK.register(phase, this::onServerTick);
    }

    public void onServerTick(MinecraftServer server) {
        ServerTickRateTracker tickRateTracker = CitadelServerData.get(server).getOrCreateTickRateTracker();
        if (server instanceof ModifiableTickRateServer modifiableServer) {
            long l = tickRateTracker.getServerTickLengthMs();
            if (l == MinecraftServer.MS_PER_TICK) {
                modifiableServer.resetGlobalTickLengthMs();
            } else {
                modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
            }
            tickRateTracker.masterTick();
        }
    }

    /*
        Biome gen example. Place
        ExpandedBiomes.addExpandedBiome(Biomes.WARPED_FOREST, LevelStem.OVERWORLD);
        In mod's constructor in order to work before trying something similar to this.

    @SubscribeEvent
    public void onReplaceBiome(EventReplaceBiome event){
        if(event.weirdness > 0.5F && event.weirdness < 1F && event.depth > 0.2F && event.depth < 0.9F){
            event.setResult(Event.Result.ALLOW);
            event.setBiomeToGenerate(event.getBiomeSource().getResourceKeyMap().get(Biomes.WARPED_FOREST));
        }
    }
    */

    public boolean canEntityTickClient(Level level, Entity entity) {
        return true;
    }

    public boolean canEntityTickServer(Level level, Entity entity) {
        if (level instanceof ServerLevel) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(((ServerLevel) level).getServer());
            if (tracker.isTickingHandled(entity)) {
                return false;
            } else if (!tracker.hasNormalTickRate(entity)) {
                EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
                var result = EventChangeEntityTickRate.EVENT.invoker().onChangeEntityTickRate(event);
                if (result.isFalse()) {
                    return true;
                } else {
                    tracker.addTickBlockedEntity(entity);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGamePaused() {
        return false;
    }

    public float getMouseOverProgress(ItemStack itemStack) {
        return 0.0F;
    }

    public Player getClientSidePlayer() {
        return null;
    }
}
