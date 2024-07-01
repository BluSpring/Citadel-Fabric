package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Supplier;

public class SyncClientTickRateMessage implements CitadelPacket {
    private CompoundTag compound;

    public SyncClientTickRateMessage(CompoundTag compound) {
        this.compound = compound;
    }

    public CompoundTag getCompound() {
        return compound;
    }

    public static void write(SyncClientTickRateMessage message, FriendlyByteBuf packetBuffer) {
        PacketBufferUtils.writeTag(packetBuffer, message.compound);
    }

    public static SyncClientTickRateMessage read(FriendlyByteBuf packetBuffer) {
        return new SyncClientTickRateMessage(PacketBufferUtils.readTag(packetBuffer));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CitadelMessages.SYNC_CLIENT_TICK_RATE_TYPE;
    }

    @Override
    public void handleClient() {
        Citadel.PROXY.handleClientTickRatePacket(this.compound);
    }
}