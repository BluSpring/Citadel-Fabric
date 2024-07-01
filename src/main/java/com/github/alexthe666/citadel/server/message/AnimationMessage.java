package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class AnimationMessage implements CitadelPacket {

    private int entityID;
    private int index;

    public AnimationMessage(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CitadelMessages.ANIMATION_TYPE;
    }

    @Override
    public void handleClient() {
        Citadel.PROXY.handleAnimationPacket(this.entityID, this.index);
    }

    @Override
    public void handleServer(ServerPlayer sender) {
        Citadel.PROXY.handleAnimationPacket(this.entityID, this.index);
    }

    public int getEntityID() {
        return entityID;
    }

    public int getIndex() {
        return index;
    }
}
