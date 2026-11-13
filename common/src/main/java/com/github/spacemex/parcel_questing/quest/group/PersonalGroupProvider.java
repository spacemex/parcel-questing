package com.github.spacemex.parcel_questing.quest.group;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public final class PersonalGroupProvider implements QuestGroupProvider {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "personal");

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public Optional<UUID> getGroupId(ServerPlayer player) {
        return Optional.of(player.getUUID());
    }
}
