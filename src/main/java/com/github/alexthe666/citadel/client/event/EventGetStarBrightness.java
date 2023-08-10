package com.github.alexthe666.citadel.client.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;

@Environment(EnvType.CLIENT)
public class EventGetStarBrightness {
    public static final Event<GetStarBrightnessCallback> EVENT = EventFactory.createEventResult();

    public interface GetStarBrightnessCallback {
        EventResult onGetStarBrightness(EventGetStarBrightness event);
    }

    private ClientLevel clientLevel;
    private float brightness;
    private float partialTicks;

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
}
