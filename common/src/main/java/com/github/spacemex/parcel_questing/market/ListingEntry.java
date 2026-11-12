package com.github.spacemex.parcel_questing.market;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Objects;

public record ListingEntry(ItemStackTemplate stack, long price) {
    public static final Codec<ListingEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(ListingEntry::stack),
            Codec.LONG.fieldOf("price").forGetter(ListingEntry::price)
    ).apply(inst, ListingEntry::new));

    public ListingEntry {
        Objects.requireNonNull(stack);

        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
