package com.github.alexthe666.citadel.animation;

import com.github.alexthe666.citadel.server.message.AnimationMessage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import io.github.fabricators_of_create.porting_lib.core.util.ServerLifecycleHooks;
import org.apache.commons.lang3.ArrayUtils;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * @author iLexiconn
 * @since 1.0.0
 */
public enum AnimationHandler {
    INSTANCE;

    /**
     * Sends an animation packet to all clients, notifying them of a changed animation
     *
     * @param entity    the entity with an animation to be updated
     * @param animation the animation to be updated
     * @param <T>       the entity type
     */
    public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (entity.level().isClientSide) {
            return;
        }
        entity.setAnimation(animation);

        var packet = new AnimationMessage(entity.getId(), ArrayUtils.indexOf(entity.getAnimations(), animation));
        for (ServerPlayer p : PlayerLookup.all(ServerLifecycleHooks.getCurrentServer())) {
            ServerPlayNetworking.send(p, packet);
        }
    }

    /**
     * Updates all animations for a given entity
     *
     * @param entity the entity with an animation to be updated
     * @param <T>    the entity type
     */
    public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
        if (entity.getAnimation() == null) {
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        } else {
            if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
                if (entity.getAnimationTick() == 0) {
                    AnimationEvent.Start event = new AnimationEvent.Start<>(entity, entity.getAnimation());
                    if (!event.post()) {
                        this.sendAnimationMessage(entity, event.getAnimation());
                    }
                }
                if (entity.getAnimationTick() < entity.getAnimation().getDuration()) {
                    entity.setAnimationTick(entity.getAnimationTick() + 1);
                    (new AnimationEvent.Tick<>(entity, entity.getAnimation(), entity.getAnimationTick())).sendEvent();
                }
                if (entity.getAnimationTick() == entity.getAnimation().getDuration()) {
                    entity.setAnimationTick(0);
                    entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                }
            }
        }
    }
}