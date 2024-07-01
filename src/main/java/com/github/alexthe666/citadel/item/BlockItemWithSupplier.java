package com.github.alexthe666.citadel.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class BlockItemWithSupplier extends BlockItem {

    private final RegistrySupplier<Block> blockSupplier;

    public BlockItemWithSupplier(RegistrySupplier<Block> blockSupplier, Properties props) {
        super(null, props);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public Block getBlock() {
        return blockSupplier.get();
    }

    public boolean canFitInsideContainerItems() {
        return !(blockSupplier.get() instanceof ShulkerBoxBlock);
    }

    public void onDestroyed(ItemEntity p_150700_) {
        if (this.blockSupplier.get() instanceof ShulkerBoxBlock) {
            ItemStack itemstack = p_150700_.getItem();

            if (itemstack.has(DataComponents.BLOCK_ENTITY_DATA)) {
                var tag = itemstack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();

                if (!tag.contains("Items", 10))
                    return;

                ListTag listtag = tag.getList("Items", 10);
                ItemUtils.onContainerDestroyed(p_150700_, listtag.stream().map(CompoundTag.class::cast).map(t -> ItemStack.CODEC.parse(NbtOps.INSTANCE, t).getOrThrow()).toList());
            }
        }
    }
}
