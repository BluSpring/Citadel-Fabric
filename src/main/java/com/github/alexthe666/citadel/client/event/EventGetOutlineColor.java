package com.github.alexthe666.citadel.client.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class EventGetOutlineColor {
    public static final Event<GetOutlineColorCallback> EVENT = EventFactory.createEventResult();

    public interface GetOutlineColorCallback {
        EventResult onGetOutlineColor(EventGetOutlineColor event);
    }

    private Entity entityIn;
    private int color;

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


}