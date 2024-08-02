package com.github.alexthe666.citadel.animation;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class AnimationEvent<T extends Entity & IAnimatedEntity> {
    public static final Event<StartCallback> START = EventFactory.createArrayBacked(StartCallback.class, callbacks -> event -> {
        for (StartCallback callback : callbacks) {
            if (callback.onAnimationStart(event)) {
                return true;
            }
        }

        return false;
    });

    public static final Event<TickCallback> TICK = EventFactory.createArrayBacked(TickCallback.class, callbacks -> event -> {
        for (TickCallback callback : callbacks) {
            callback.onAnimationTick(event);
        }
    });

    @FunctionalInterface
    public interface StartCallback {
        boolean onAnimationStart(Start<?> event);
    }

    @FunctionalInterface
    public interface TickCallback {
        void onAnimationTick(Tick<?> event);
    }

    protected Animation animation;
    private T entity;

    AnimationEvent(T entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
    }

    public T getEntity() {
        return this.entity;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public static class Start<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }
    }

    public static class Tick<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        protected int tick;

        public Tick(T entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return this.tick;
        }
    }
}