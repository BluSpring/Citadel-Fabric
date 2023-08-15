package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PropertiesMessage implements S2CPacket, C2SPacket {
    private String propertyID;
    private CompoundTag compound;
    private int entityID;

    public PropertiesMessage(String propertyID, CompoundTag compound, int entityID) {
        this.propertyID = propertyID;
        this.compound = compound;
        this.entityID = entityID;
    }

    public static void write(PropertiesMessage message, FriendlyByteBuf packetBuffer) {
        PacketBufferUtils.writeUTF8String(packetBuffer, message.propertyID);
        PacketBufferUtils.writeTag(packetBuffer, message.compound);
        packetBuffer.writeInt(message.entityID);
    }

    public static PropertiesMessage read(FriendlyByteBuf packetBuffer) {
        return new PropertiesMessage(PacketBufferUtils.readUTF8String(packetBuffer), PacketBufferUtils.readTag(packetBuffer), packetBuffer.readInt());
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
        Handler.handle(this, server, player);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        Handler.handle(this, client, client.player);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        write(this, buf);
    }

    public static class Handler {

        public static void handle(final PropertiesMessage message, BlockableEventLoop<?> loop, Player player) {
            loop.execute(() -> {
                if (!(loop instanceof MinecraftServer)) {
                    Citadel.PROXY.handlePropertiesPacket(message.propertyID, message.compound, message.entityID);
                } else {
                    Entity e = player.level.getEntity(message.entityID);
                    if (e instanceof LivingEntity && (message.propertyID.equals("CitadelPatreonConfig") || message.propertyID.equals("CitadelTagUpdate"))) {
                        CitadelEntityData.setCitadelTag((LivingEntity) e, message.compound);

                    }
                }
            });
        }
    }
}