package com.github.alexthe666.citadel.client.event;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

public class EventPosePlayerHand extends BaseEvent {
    public interface Callback {
        void onPosePlayerHand(EventPosePlayerHand event);
    }

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onPosePlayerHand(event);
        }
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onPosePlayerHand(this);
    }

    private LivingEntity entityIn;
    private HumanoidModel model;
    private boolean left;
    private TriState result = TriState.DEFAULT;

    public EventPosePlayerHand(LivingEntity entityIn, HumanoidModel model, boolean left) {
        this.entityIn = entityIn;
        this.model = model;
        this.left = left;
    }

    public Entity getEntityIn() {
        return entityIn;
    }

    public HumanoidModel getModel() {
        return model;
    }

    public boolean isLeftHand() {
        return left;
    }

    public void setResult(TriState result) {
        this.result = result;
    }

    public TriState getResult() {
        return result;
    }
}
