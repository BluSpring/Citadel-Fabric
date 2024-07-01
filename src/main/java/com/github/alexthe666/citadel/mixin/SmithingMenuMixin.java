package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {
    @WrapOperation(
            method = "createResult()V",
            remap = CitadelConstants.REMAPREFS, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/List;")
    )
    private <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> citadel_getRecipesFor(RecipeManager instance, RecipeType<T> type, I input, Level level, Operation<List<RecipeHolder<T>>> original) {
        //noinspection MixinExtrasOperationParameters
        List<RecipeHolder<T>> list = new ArrayList<>(original.call(instance, type, input, level));

        if(type == RecipeType.SMITHING && input.size() >= 2 && !input.getItem(0).isEmpty()&& !input.getItem(1).isEmpty()){
            list.addAll((List) CitadelRecipes.getSmithingRecipes());
        }
        return list;
    }
}