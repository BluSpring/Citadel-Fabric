package com.github.alexthe666.citadel.animation;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;

import net.minecraft.world.entity.Entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public abstract class AnimationEvent<T extends Entity & IAnimatedEntity> extends BaseEvent {
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

    public static class Start<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> implements CancellableEvent {
        public interface Callback {
            void onAnimationStart(Start<?> event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
            for (Callback callback : callbacks) {
                callback.onAnimationStart(event);
            }
        });

        @Override
        public void sendEvent() {
            EVENT.invoker().onAnimationStart(this);
        }

        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }
    }

    public static class Tick<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        public interface Callback {
            void onAnimationTick(Tick<?> event);
        }

        public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
            for (Callback callback : callbacks) {
                callback.onAnimationTick(event);
            }
        });

        @Override
        public void sendEvent() {
            EVENT.invoker().onAnimationTick(this);
        }

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