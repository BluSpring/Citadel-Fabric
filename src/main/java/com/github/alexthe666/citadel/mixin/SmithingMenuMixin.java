package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.item.CitadelRecipes;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
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
            remap = CitadelConstants.REMAPREFS, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipesFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/List;")
    )
    private <T extends Recipe<?>, C extends Container> List<T> citadel_getRecipesFor(RecipeManager instance, RecipeType<T> type, C container, Level level, Operation<List<T>> original) {
        List<T> list = new ArrayList<>();
        list.addAll(original.call(instance, type, container, level));
        if(type == RecipeType.SMITHING && container.getContainerSize() >= 2 && !container.getItem(0).isEmpty()&& !container.getItem(1).isEmpty()){
            list.addAll((List<T>) CitadelRecipes.getSmithingRecipes());
        }
        return list;
    }
}