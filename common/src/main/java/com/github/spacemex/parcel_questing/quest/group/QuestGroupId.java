package com.github.spacemex.parcel_questing.quest.group;

import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.UUID;

public record QuestGroupId(Identifier provider, UUID id) {
    public QuestGroupId {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(id, "id");
    }
}
