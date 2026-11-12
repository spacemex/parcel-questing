package com.github.spacemex.parcel_questing.api.rewards;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record ItemReward(ItemStack stack) implements BaseReward {

    public ItemReward {
        Objects.requireNonNull(stack);

        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Item stack cannot be empty.");
        }

        stack = stack.copy();
    }

    @Override
    public ItemStack stack() {
        return stack.copy();
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "item");
    }
}
