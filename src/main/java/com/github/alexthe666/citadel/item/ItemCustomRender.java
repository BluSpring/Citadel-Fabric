package com.github.alexthe666.citadel.item;

import java.util.function.Consumer;

import com.github.alexthe666.citadel.Citadel;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import net.minecraft.world.item.Item;

public class ItemCustomRender extends Item {

    public ItemCustomRender(Properties props) {
        super(props);
    }

    @SuppressWarnings("removal")
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(((IClientItemExtensions) Citadel.PROXY.getISTERProperties()));
    }
}
