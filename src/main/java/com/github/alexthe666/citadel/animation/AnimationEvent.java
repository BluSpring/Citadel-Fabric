package com.github.alexthe666.citadel.animation;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class AnimationEvent<T extends Entity & IAnimatedEntity> {
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

    public static final Event<StartCallback> START = EventFactory.createArrayBacked(StartCallback.class, callbacks -> (start) -> {
        var isCancelled = false;

        for (StartCallback callback : callbacks) {
            if (!isCancelled)
                callback.onStart(start);
        }

        return isCancelled;
    });

    public interface StartCallback {
        boolean onStart(Start<?> event);
    }

    public static class Start<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }
    }

    public static final Event<TickCallback> TICK = EventFactory.createArrayBacked(TickCallback.class, callbacks -> (event) -> {
        for (TickCallback callback : callbacks) {
            callback.onTick(event);
        }
    });

    public interface TickCallback {
        void onTick(Tick<?> event);
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