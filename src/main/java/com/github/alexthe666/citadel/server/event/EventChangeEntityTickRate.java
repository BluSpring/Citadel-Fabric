package com.github.alexthe666.citadel.server.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.world.entity.Entity;

public class EventChangeEntityTickRate {
    public static final Event<ChangeEntityTickRateCallback> EVENT = EventFactory.createEventResult();

    public interface ChangeEntityTickRateCallback {
        EventResult onChangeEntityTickRate(EventChangeEntityTickRate event);
    }

    private Entity entity;
    private float targetTickRate;

    public EventChangeEntityTickRate(Entity entity, float targetTickRate) {
        this.entity = entity;
        this.targetTickRate = targetTickRate;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getTargetTickRate() {
        return targetTickRate;
    }
}
