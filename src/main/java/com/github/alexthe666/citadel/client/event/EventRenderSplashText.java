package com.github.alexthe666.citadel.client.event;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

import net.minecraft.client.gui.GuiGraphics;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

public abstract class EventRenderSplashText extends BaseEvent {
    private String splashText;

    private GuiGraphics guiGraphics;
    private float partialTicks;

    public EventRenderSplashText(String splashText, GuiGraphics guiGraphics, float partialTicks) {
        this.splashText = splashText;
        this.guiGraphics = guiGraphics;
        this.partialTicks = partialTicks;
    }

    public String getSplashText() {
        return splashText;
    }

    public void setSplashText(String splashText) {
        this.splashText = splashText;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public static class Pre extends EventRenderSplashText {
        public interface Callback {
            void preRenderSplashText(Pre event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
            for (Callback callback : callbacks) {
                callback.preRenderSplashText(event);
            }
        });

        @Override
        public void sendEvent() {
            EVENT.invoker().preRenderSplashText(this);
        }

        private TriState result = TriState.DEFAULT;

        private int splashTextColor;

        public Pre(String splashText, GuiGraphics guiGraphics, float partialTicks, int splashTextColor) {
            super(splashText, guiGraphics, partialTicks);
            this.splashTextColor = splashTextColor;
        }

        public int getSplashTextColor() {
            return splashTextColor;
        }

        public void setSplashTextColor(int splashTextColor) {
            this.splashTextColor = splashTextColor;
        }

        public void setResult(TriState result) {
            this.result = result;
        }

        public TriState getResult() {
            return result;
        }
    }

    public static class Post extends EventRenderSplashText {
        public interface Callback {
            void postRenderSplashText(Post event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
            for (Callback callback : callbacks) {
                callback.postRenderSplashText(event);
            }
        });

        @Override
        public void sendEvent() {
            EVENT.invoker().postRenderSplashText(this);
        }

        public Post(String splashText, GuiGraphics guiGraphics, float partialTicks) {
            super(splashText, guiGraphics, partialTicks);
        }
    }

}
