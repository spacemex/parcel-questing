package com.github.spacemex.parcel_questing.quest;

import com.github.spacemex.parcel_questing.api.registry.RequirementRegistry;
import com.github.spacemex.parcel_questing.api.registry.RewardRegistry;
import com.github.spacemex.parcel_questing.api.requirement.Requirement;
import com.github.spacemex.parcel_questing.api.reward.BaseReward;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Quest(Identifier id, String title, String description, Identifier type, Issuer issuer,
                    int requiredLevel, int maxLevel, Repeat repeat, boolean forceQuest, List<Identifier> depends,
                    List<Requirement> requirements, List<BaseReward> rewards) {


    public static final Codec<Quest> CODEC = RecordCodecBuilder.<Quest>create(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(Quest::id),
            Codec.STRING.fieldOf("title").forGetter(Quest::title),
            Codec.STRING.fieldOf("description").forGetter(Quest::description),
            Identifier.CODEC.fieldOf("type").forGetter(Quest::type),
            Issuer.CODEC.fieldOf("issuer").forGetter(Quest::issuer),
            Codec.INT.fieldOf("requiredLevel").forGetter(Quest::requiredLevel),
            Codec.INT.fieldOf("maxLevel").forGetter(Quest::maxLevel),
            Repeat.CODEC.fieldOf("repeat").forGetter(Quest::repeat),
            Codec.BOOL.fieldOf("forceQuest").forGetter(Quest::forceQuest),
            Identifier.CODEC.listOf().fieldOf("depends").forGetter(Quest::depends),
            RequirementRegistry.CODEC.listOf().fieldOf("requirements").forGetter(Quest::requirements),
            RewardRegistry.CODEC.listOf().fieldOf("rewards").forGetter(Quest::rewards)
    ).apply(inst, Quest::new)).flatXmap(Quest::validate, DataResult::success);


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


    private static DataResult<Quest> validate(Quest quest) {
        Set<String> ids = new HashSet<>();

        for (Requirement requirement : quest.requirements()) {
            String id = requirement.uniqueIdentifier();

            if (!ids.add(id)) {
                return DataResult.error(() -> "Duplicate requirement ID '" + id + "' in quest " + quest.id());
            }
        }

        if (quest.depends().contains(quest.id())) {
            return DataResult.error(() -> "Quest cannot depend on itself: " + quest.id());
        }

        return DataResult.success(quest);
    }

}
