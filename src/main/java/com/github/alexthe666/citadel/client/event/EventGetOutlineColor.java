package com.github.alexthe666.citadel.client.event;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

import net.minecraft.world.entity.Entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

public class EventGetOutlineColor extends BaseEvent {
    public interface Callback {
        void onGetOutlineColor(EventGetOutlineColor event);
    }

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onGetOutlineColor(event);
        }
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onGetOutlineColor(this);
    }

    private Entity entityIn;
    private int color;
    private TriState result = TriState.DEFAULT;

    public EventGetOutlineColor(Entity entityIn, int color) {
        this.entityIn = entityIn;
        this.color = color;
    }

    public Entity getEntityIn() {
        return entityIn;
    }

    public void setEntityIn(Entity entityIn) {
        this.entityIn = entityIn;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setResult(TriState result) {
        this.result = result;
    }

    public TriState getResult() {
        return result;
    }
}
