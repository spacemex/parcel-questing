package com.github.spacemex.parcel_questing.api.rewards;

import net.minecraft.resources.Identifier;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record MoneyReward(long amount) implements BaseReward {
    public MoneyReward {
        if (amount < 0) {
            throw new IllegalStateException("amount cannot be negative");
        }
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "money");
    }
}
