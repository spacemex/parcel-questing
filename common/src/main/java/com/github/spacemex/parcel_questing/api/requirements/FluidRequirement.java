package com.github.spacemex.parcel_questing.api.requirements;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public record FluidRequirement(String uniqueId, TargetType targetType, Identifier target,
                               long amount) implements CountedRequirement {
    public FluidRequirement {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(targetType, "targetType");

        if (targetType != TargetType.FLUID && targetType != TargetType.TAG) {
            throw new IllegalArgumentException("targetType must be either FLUID or TAG");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Fluid requirement amount must be greater than zero.");
        }
    }

    public static FluidRequirement fluid(String uniqueId, Identifier fluid, long amount) {
        return new FluidRequirement(uniqueId, TargetType.FLUID, fluid, amount);
    }

    public static FluidRequirement tag(String uniqueId, Identifier tag, long amount) {
        return new FluidRequirement(uniqueId, TargetType.TAG, tag, amount);
    }

    public boolean isFluid() {
        return targetType == TargetType.FLUID;
    }

    public boolean isTag() {
        return targetType == TargetType.TAG;
    }

    @Override
    public String UniqueIdentifier() {
        return uniqueId;
    }

    @Override
    public String progressKey() {
        return "fluid/" + targetType.name().toLowerCase() + "/" + target;
    }
}
