package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.forge.extensions.IClientItemExtensions;
import com.github.alexthe666.citadel.forge.extensions.ItemRenderExtension;
import net.minecraft.world.item.Item;

public class ItemCustomRender extends Item implements ItemRenderExtension {

    public ItemCustomRender(Properties props) {
        super(props);
    }

    @Override
    public void citadel$initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept(((IClientItemExtensions) Citadel.PROXY.getISTERProperties()));
    }
}
