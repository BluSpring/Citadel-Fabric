package com.github.alexthe666.citadel.mixin.client;

import java.util.function.Supplier;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventGetStarBrightness;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

import net.fabricmc.fabric.api.util.TriState;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level {

    protected ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> levelResourceKey, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeHolder, Supplier<ProfilerFiller> filler, boolean b1, boolean b2, long seed, int i) {
        super(writableLevelData, levelResourceKey, registryAccess, dimensionTypeHolder, filler, b1, b2, seed, i);
    }

    @ModifyReturnValue(at = @At("RETURN"), remap = CitadelConstants.REMAPREFS, method = "getStarBrightness")
    private float citadel_getStarBrightness(float original, @Local(argsOnly = true) float partialTicks) {
        EventGetStarBrightness event = new EventGetStarBrightness(((ClientLevel) (Object) this), original, partialTicks);
        event.sendEvent();
        if (event.getResult() == TriState.TRUE) {
            return event.getBrightness();
        }

        return original;
    }

    @ModifyExpressionValue(
            method = "tickTime",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "CONSTANT", args = "longValue=1"),
            expect = 2)
    private long citadel_clientSetDayTime(long timeIn) {
        return ClientTickRateTracker.getForClient(Minecraft.getInstance()).getDayTimeIncrement(timeIn);
    }
}

