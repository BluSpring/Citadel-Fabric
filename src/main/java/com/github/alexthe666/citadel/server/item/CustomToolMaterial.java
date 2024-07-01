package com.github.alexthe666.citadel.server.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.Locale;

public class CustomToolMaterial implements Tier {
   private String name;
   private int harvestLevel;
   private int durability;
   private float damage;
   private float speed;
   private int enchantability;
    private Ingredient ingredient = null;
    private TagKey<Block> key;

    public CustomToolMaterial(String name, int harvestLevel, int durability, float damage, float speed, int enchantability) {
        this.name = name;
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.damage = damage;
        this.speed = speed;
        this.enchantability = enchantability;
        this.key = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("citadel", name.toLowerCase(Locale.ROOT) + "_incorrect"));
    }

    public String getName() {
        return name;
    }

    @Override
    public int getUses() {
        return durability;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    /*@Override
    public int getLevel() {
        return harvestLevel;
    }*/

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return key;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return ingredient == null ? Ingredient.EMPTY : ingredient;
    }

    public void setRepairMaterial(Ingredient ingredient){
        this.ingredient = ingredient;
    }
}
