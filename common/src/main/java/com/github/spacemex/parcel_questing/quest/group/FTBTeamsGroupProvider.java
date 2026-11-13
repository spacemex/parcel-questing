package com.github.spacemex.parcel_questing.quest.group;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public final class FTBTeamsGroupProvider implements QuestGroupProvider {
    private static final Identifier ID = Identifier.fromNamespaceAndPath("ftbteams", "team");

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public Optional<UUID> getGroupId(ServerPlayer player) {
        return FTBTeamsAPI.api().getManager().getTeamForPlayer(player).map(Team::getId);
    }
}
