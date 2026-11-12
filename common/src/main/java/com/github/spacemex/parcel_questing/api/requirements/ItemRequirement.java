package com.github.spacemex.parcel_questing.api.requirements;

import net.minecraft.resources.Identifier;

import java.util.Objects;

public record ItemRequirement(String uniqueId, TargetType targetType, Identifier target,
                              long amount) implements CountedRequirement {
    public ItemRequirement {
        Objects.requireNonNull(targetType, "targetType");
        Objects.requireNonNull(target, "target");

        if (targetType != TargetType.ITEM && targetType != TargetType.TAG) {
            throw new IllegalArgumentException("targetType must be either ITEM or TAG");
        }

        if (amount <= 0L) {
            throw new IllegalArgumentException("Item requirement amount must be greater than zero.");
        }
    }

    public static ItemRequirement item(String uniqueId, Identifier item, long amount) {
        return new ItemRequirement(uniqueId, TargetType.ITEM, item, amount);
    }

    public static ItemRequirement tag(String uniqueId, Identifier tag, long amount) {
        return new ItemRequirement(uniqueId, TargetType.TAG, tag, amount);
    }

    public boolean isItem() {
        return targetType == TargetType.ITEM;
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
        return "item/" + targetType.name().toLowerCase() + "/" + target;
    }
}
