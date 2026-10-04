package com.github.alexthe666.citadel.client.event;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

import net.minecraft.client.multiplayer.ClientLevel;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

public class EventGetStarBrightness extends BaseEvent {
    public interface Callback {
        void onGetStarBrightness(EventGetStarBrightness event);
    }

    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.onGetStarBrightness(event);
        }
    });

    @Override
    public void sendEvent() {
        EVENT.invoker().onGetStarBrightness(this);
    }

    private ClientLevel clientLevel;
    private float brightness;
    private float partialTicks;
    private TriState result = TriState.DEFAULT;

    public EventGetStarBrightness(ClientLevel clientLevel, float brightness, float partialTicks) {
        this.clientLevel = clientLevel;
        this.brightness = brightness;
        this.partialTicks = partialTicks;
    }

    public ClientLevel getLevel() {
        return clientLevel;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setResult(TriState result) {
        this.result = result;
    }

    public TriState getResult() {
        return result;
    }
}
