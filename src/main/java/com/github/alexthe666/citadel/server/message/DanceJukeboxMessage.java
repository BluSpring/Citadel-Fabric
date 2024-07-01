package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class DanceJukeboxMessage implements CitadelPacket {

    public int entityID;
    public boolean dance;
    public BlockPos jukeBox;

    public DanceJukeboxMessage(int entityID, boolean dance, BlockPos jukeBox) {
        this.entityID = entityID;
        this.dance = dance;
        this.jukeBox = jukeBox;
    }

    public DanceJukeboxMessage() {
    }

    public int getEntityID() {
        return entityID;
    }

    public boolean isDance() {
        return dance;
    }

    public BlockPos getJukeBox() {
        return jukeBox;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CitadelMessages.DANCE_JUKEBOX_TYPE;
    }

    @Override
    public void handleClient() {
        handle(Citadel.PROXY.getClientSidePlayer());
    }

    @Override
    public void handleServer(ServerPlayer sender) {
        handle(sender);
    }

    private void handle(Player player) {
        if (player != null) {
            Citadel.PROXY.handleJukeboxPacket(player.level(), this.entityID, this.jukeBox, this.dance);
        }
    }
}