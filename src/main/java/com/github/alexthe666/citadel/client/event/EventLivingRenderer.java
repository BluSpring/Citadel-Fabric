package com.github.alexthe666.citadel.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public class EventLivingRenderer {
    public static final Event<LivingRendererCallback> EVENT = EventFactory.createLoop();

    public interface LivingRendererCallback {
        void onLivingRender(EventLivingRenderer event);
    }

    public static final Event<SetupRotationsCallback> SETUP_ROTATIONS = EventFactory.createLoop();

    public interface SetupRotationsCallback {
        void onSetupRotations(SetupRotations event);
    }

    public static final Event<AccessToBufferSourceCallback> ACCESS_TO_BUFFER_SOURCE = EventFactory.createLoop();

    public interface AccessToBufferSourceCallback {
        void onAccessBufferSource(AccessToBufferSource event);
    }

    public static final Event<PreSetupAnimationsCallback> SETUP_ANIMATIONS_PRE = EventFactory.createLoop();

    public interface PreSetupAnimationsCallback {
        void onPreSetupAnimations(PreSetupAnimations event);
    }

    public static final Event<PostSetupAnimationsCallback> SETUP_ANIMATIONS_POST = EventFactory.createLoop();

    public interface PostSetupAnimationsCallback {
        void onPostSetupAnimations(PostSetupAnimations event);
    }

    public static final Event<PostRenderModelCallback> RENDER_MODEL_POST = EventFactory.createLoop();

    public interface PostRenderModelCallback {
        void onPostRenderModel(PostRenderModel event);
    }

    private LivingEntity entity;
    private EntityModel model;
    private PoseStack poseStack;
    private float partialTicks;

    public EventLivingRenderer(LivingEntity entity, EntityModel model, PoseStack poseStack, float partialTicks) {
        this.entity = entity;
        this.model = model;
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public EntityModel getModel() {
        return model;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public static class SetupRotations extends EventLivingRenderer {
        private float bodyYRot;

        public SetupRotations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks) {
            super(entity, model, poseStack, partialTicks);
            this.bodyYRot = bodyYRot;
        }

        public float getBodyYRot() {
            return bodyYRot;
        }
    }

    public static class AccessToBufferSource extends EventLivingRenderer {
        private float bodyYRot;
        private MultiBufferSource bufferSource;
        private int packedLight;

        public AccessToBufferSource(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, partialTicks);
            this.bodyYRot = bodyYRot;
            this.bufferSource = bufferSource;
            this.packedLight = packedLight;
        }

        public float getBodyYRot() {
            return bodyYRot;
        }

        public MultiBufferSource getBufferSource() {
            return bufferSource;
        }

        public int getPackedLight() {
            return packedLight;
        }
    }

    public static class PreSetupAnimations extends AccessToBufferSource {

        public PreSetupAnimations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, bufferSource, packedLight);
        }
    }

    public static class PostSetupAnimations extends AccessToBufferSource {

        public PostSetupAnimations(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, bufferSource, packedLight);
        }
    }

    public static class PostRenderModel extends AccessToBufferSource {

        public PostRenderModel(LivingEntity entity, EntityModel model, PoseStack poseStack, float bodyYRot, float partialTicks, MultiBufferSource bufferSource, int packedLight) {
            super(entity, model, poseStack, bodyYRot, partialTicks, bufferSource, packedLight);
        }
    }
}
