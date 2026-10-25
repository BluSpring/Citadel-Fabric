package com.github.alexthe666.citadel.client.gui;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import com.github.alexthe666.citadel.client.gui.data.EntityRenderData;
import com.github.alexthe666.citadel.client.gui.data.ImageData;
import com.github.alexthe666.citadel.client.gui.data.ItemRenderData;
import com.github.alexthe666.citadel.client.gui.data.LinkData;
import com.github.alexthe666.citadel.client.gui.data.RecipeData;
import com.github.alexthe666.citadel.client.gui.data.TabulaRenderData;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.TranslatableContents;

public record BookPage(
    Optional<String> parent, String textFileToReadFrom,

    List<LinkData> linkedButtons, List<EntityLinkData> linkedEntities,
    List<ItemRenderData> itemRenders, List<RecipeData> recipes,
    List<TabulaRenderData> tabulaRenders, List<EntityRenderData> entityRenders,
    List<ImageData> images,

    Component title
) {
    public static final Codec<BookPage> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING
                .optionalFieldOf("parent")
                .forGetter(BookPage::parent),
            Codec.STRING
                .fieldOf("text")
                .forGetter(BookPage::textFileToReadFrom),

            LinkData.CODEC.listOf()
                .optionalFieldOf("linked_page_buttons", Collections.emptyList())
                .forGetter(BookPage::linkedButtons),
            EntityLinkData.CODEC.listOf()
                .optionalFieldOf("entity_buttons", Collections.emptyList())
                .forGetter(BookPage::linkedEntities),
            ItemRenderData.CODEC.listOf()
                .optionalFieldOf("item_renders", Collections.emptyList())
                .forGetter(BookPage::itemRenders),
            RecipeData.CODEC.listOf()
                .optionalFieldOf("recipes", Collections.emptyList())
                .forGetter(BookPage::recipes),
            TabulaRenderData.CODEC.listOf()
                .optionalFieldOf("tabula_renders", Collections.emptyList())
                .forGetter(BookPage::tabulaRenders),
            EntityRenderData.CODEC.listOf()
                .optionalFieldOf("entity_renders", Collections.emptyList())
                .forGetter(BookPage::entityRenders),
            ImageData.CODEC.listOf()
                .optionalFieldOf("images", Collections.emptyList())
                .forGetter(BookPage::images),

            // Support both "title_locale" (string -> translatable) and "title" (string or Component)
            Codec.mapEither(
                Codec.STRING
                    .xmap(Component::translatable, component -> {
                        if (component.getContents() instanceof TranslatableContents translatableContents) {
                            return translatableContents.getKey();
                        } else {
                            return component.getString();
                        }
                    })
                    .fieldOf("title_locale"),
                Codec.either(
                        Codec.STRING.xmap(Component::translatable, c -> c.getString()),
                        ComponentSerialization.CODEC
                    )
                    .xmap(Either::unwrap, Either::right)
                    .fieldOf("title")
            )
                .xmap(Either::unwrap, Either::right)
                .forGetter(BookPage::title)
        )
            .apply(instance, BookPage::new)
    );
}
