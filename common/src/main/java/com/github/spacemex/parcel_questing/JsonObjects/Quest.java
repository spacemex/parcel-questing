package com.github.spacemex.parcel_questing.JsonObjects;

import com.github.spacemex.parcel_questing.api.requirements.Requirement;
import com.github.spacemex.parcel_questing.api.rewards.BaseReward;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Objects;

public record Quest(Identifier id, String title, String description, Identifier type, Issuer issuer,
                    int requiredLevel, int maxLevel, Repeat repeat, boolean forceQuest, List<Identifier> depends,
                    List<Requirement> requirements, List<BaseReward> rewards) {

    public Quest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(description, "description");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(issuer, "issuer");
        Objects.requireNonNull(repeat, "repeat");

        if (requiredLevel < 0) {
            throw new IllegalArgumentException("Required level cannot be negative.");
        }

        if (maxLevel < requiredLevel) {
            throw new IllegalArgumentException("Maximum level cannot be less than required level.");
        }

        depends = List.copyOf(depends);
        requirements = List.copyOf(requirements);
        rewards = List.copyOf(rewards);
    }
}
