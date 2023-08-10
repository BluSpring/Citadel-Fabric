package com.github.alexthe666.citadel.mixin.fabric;

import com.github.alexthe666.citadel.forge.extensions.ItemRenderExtension;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements ItemRenderExtension {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void citadel$initClient(Item.Properties properties, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            initializeClient((props) -> {
                this.renderProperties = props;
            });
        }
    }

    @Unique
    private Object renderProperties;

    @Override
    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }
}
