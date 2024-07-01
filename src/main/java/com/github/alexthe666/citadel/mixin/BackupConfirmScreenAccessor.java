package com.github.alexthe666.citadel.mixin;

import net.minecraft.client.gui.screens.BackupConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BackupConfirmScreen.class)
public interface BackupConfirmScreenAccessor {
    @Accessor
    BackupConfirmScreen.Listener getOnProceed();
}
