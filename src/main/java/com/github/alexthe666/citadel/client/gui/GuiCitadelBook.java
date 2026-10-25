package com.github.alexthe666.citadel.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class GuiCitadelBook extends GuiBasicBook {

    public GuiCitadelBook(ItemStack bookStack) {
        super(bookStack, Component.translatable("citadel_guide_book.title"));
    }

    @Override
    protected int getBindingColor() {
        return 0X64A27B;
    }

    @Override
    public Identifier getRootPage() {
        return Identifier.fromNamespaceAndPath("citadel", "book/citadel_book/root.json");
    }

    @Override
    public Identifier getTextFileDirectory() {
        return Identifier.fromNamespaceAndPath("citadel", "book/citadel_book/");
    }
}
