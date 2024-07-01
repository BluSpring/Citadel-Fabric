package com.github.alexthe666.citadel.server.message;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface CitadelPacket extends CustomPacketPayload {
    default void handleClient() {}
    default void handleServer(ServerPlayer sender) {}
}
