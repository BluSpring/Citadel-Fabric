package com.github.alexthe666.citadel.mixin;

import net.minecraft.world.TickRateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TickRateManager.class)
public interface TickRateManagerAccessor {
    @Accessor
    long getNanosecondsPerTick();

    @Accessor
    void setNanosecondsPerTick(long nanosecondsPerTick);
}
