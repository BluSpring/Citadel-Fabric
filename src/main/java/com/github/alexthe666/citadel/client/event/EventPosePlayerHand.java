package com.github.alexthe666.citadel.client.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EventPosePlayerHand {
    public static final Event<PosePlayerHandCallback> EVENT = EventFactory.createEventResult();

    public interface PosePlayerHandCallback {
        EventResult onPosePlayerHand(EventPosePlayerHand event);
    }

    private LivingEntity entityIn;
    private HumanoidModel model;
    private boolean left;

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


}
