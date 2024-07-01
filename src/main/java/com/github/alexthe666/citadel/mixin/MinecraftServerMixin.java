package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.world.ModifiableTickRateServer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TimeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ModifiableTickRateServer {

    private long modifiedMsPerTick = -1;
    private long masterMs;

    @Inject(
            method = {"runServer()V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;startMetricsRecordingTick()V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel_beforeServerTick(CallbackInfo ci) {
        masterTick();
    }

    private void masterTick() {
        masterMs += 50L;

    }

    @ModifyExpressionValue(
            method = {"runServer()V"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ServerTickRateManager;nanosecondsPerTick()J"))
    private long citadel_serverMsPerTick(long value) {
        return modifiedMsPerTick == -1 ? value : modifiedMsPerTick * TimeUtil.NANOSECONDS_PER_MILLISECOND;
    }

    @Override
    public void setGlobalTickLengthMs(long msPerTick) {
        modifiedMsPerTick = msPerTick;
    }

    @Override
    public long getMasterMs() {
        return masterMs;
    }
}
