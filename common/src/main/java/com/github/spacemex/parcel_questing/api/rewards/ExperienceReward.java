package com.github.spacemex.parcel_questing.api.rewards;

import net.minecraft.resources.Identifier;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record ExperienceReward(long amount) implements BaseReward {
    public ExperienceReward {
        if (amount < 0) {
            throw new IllegalStateException("amount cannot be negative");
        }
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "group_experience");
    }
}
