package com.github.spacemex.parcel_questing.quest.group;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public interface QuestGroupProvider {
    Identifier id();

    Optional<UUID> getGroupId(ServerPlayer player);
}
