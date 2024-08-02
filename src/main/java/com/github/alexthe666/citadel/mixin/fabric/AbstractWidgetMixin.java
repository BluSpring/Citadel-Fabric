package com.github.alexthe666.citadel.mixin.fabric;


import com.github.alexthe666.citadel.fabric.extensions.ForegroundColorExtension;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractWidget.class)
public class AbstractWidgetMixin implements ForegroundColorExtension {
    @Shadow
    public boolean active;

    protected int packedFGColor = ForegroundColorExtension.UNSET_FG_COLOR;

    @Override
    public int getFGColor() {
        if (packedFGColor != ForegroundColorExtension.UNSET_FG_COLOR)
            return packedFGColor;

        return this.active ? 16777215 : 10526880; // white : light grey
    }

    @Override
    public void setFGColor(int color) {
        packedFGColor = color;
    }

    @Override
    public void clearFGColor() {
        packedFGColor = ForegroundColorExtension.UNSET_FG_COLOR;
    }
}