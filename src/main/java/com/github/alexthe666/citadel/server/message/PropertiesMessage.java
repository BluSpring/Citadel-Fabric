package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class PropertiesMessage implements CitadelPacket {
    private String propertyID;
    private CompoundTag compound;
    private int entityID;

    public PropertiesMessage(String propertyID, CompoundTag compound, int entityID) {
        this.propertyID = propertyID;
        this.compound = compound;
        this.entityID = entityID;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public CompoundTag getCompound() {
        return compound;
    }

    public int getEntityID() {
        return entityID;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CitadelMessages.PROPERTIES_TYPE;
    }

    @Override
    public void handleClient() {
        Citadel.PROXY.handlePropertiesPacket(this.propertyID, this.compound, this.entityID);
    }

    @Override
    public void handleServer(ServerPlayer sender) {
        Entity e = sender.level().getEntity(this.entityID);
        if (e instanceof LivingEntity && (this.propertyID.equals("CitadelPatreonConfig") || this.propertyID.equals("CitadelTagUpdate"))) {
            CitadelEntityData.setCitadelTag((LivingEntity) e, this.compound);
        }
    }
}