package com.github.spacemex.parcel_questing.api.rewards;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Objects;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record ItemReward(ItemStackTemplate stack) implements BaseReward {
    public static final MapCodec<ItemReward> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStackTemplate.CODEC.fieldOf("stack").forGetter(ItemReward::stack)
    ).apply(inst, ItemReward::new));

    public ItemReward {
        Objects.requireNonNull(stack);
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "item");
    }
}
