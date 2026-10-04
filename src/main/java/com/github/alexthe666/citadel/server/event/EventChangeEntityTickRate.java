package com.github.alexthe666.citadel.server.event;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;

import net.minecraft.world.entity.Entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class EventChangeEntityTickRate extends BaseEvent implements CancellableEvent {
    public interface Callback {
        void onChangeEntityTickRate(EventChangeEntityTickRate event);
    }

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onChangeEntityTickRate(event);
        }
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onChangeEntityTickRate(this);
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
