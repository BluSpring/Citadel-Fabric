package com.github.alexthe666.citadel.client.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;

@Environment(EnvType.CLIENT)
public class EventGetFluidRenderType {
    public static final Event<GetFluidRenderTypeCallback> EVENT = EventFactory.createEventResult();

    public interface GetFluidRenderTypeCallback {
        EventResult onGetFluidRenderType(EventGetFluidRenderType event);
    }

    private FluidState fluidState;
    private RenderType renderType;

    public EventGetFluidRenderType(FluidState fluidState, RenderType renderType) {
        this.fluidState = fluidState;
        this.renderType = renderType;
    }

    public FluidState getFluidState() {
        return fluidState;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(RenderType renderType) {
        this.renderType = renderType;
    }
}
