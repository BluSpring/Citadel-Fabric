package com.github.alexthe666.citadel.mixin;

import net.minecraft.client.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Timer.class)
public interface TimerAccessor {
    @Accessor
    float getMsPerTick();

    @Accessor
    @Mutable
    void setMsPerTick(float value);
}
