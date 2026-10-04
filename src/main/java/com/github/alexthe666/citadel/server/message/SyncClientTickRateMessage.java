package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SyncClientTickRateMessage implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncClientTickRateMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("citadel", "tick_rate"));
    public static final StreamCodec<FriendlyByteBuf, SyncClientTickRateMessage> CODEC = StreamCodec.ofMember(SyncClientTickRateMessage::write, SyncClientTickRateMessage::read);
    private CompoundTag compound;

    public SyncClientTickRateMessage(CompoundTag compound) {
        this.compound = compound;
    }

    public static void write(SyncClientTickRateMessage message, FriendlyByteBuf packetBuffer) {
        PacketBufferUtils.writeTag(packetBuffer, message.compound);
    }

    public static SyncClientTickRateMessage read(FriendlyByteBuf packetBuffer) {
        return new SyncClientTickRateMessage(PacketBufferUtils.readTag(packetBuffer));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public static void handle(final SyncClientTickRateMessage message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            {
                Citadel.PROXY.handleClientTickRatePacket(message.compound);
            }
        });
    }
}