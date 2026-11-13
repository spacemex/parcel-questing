package com.github.spacemex.parcel_questing.quest.group;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import dev.architectury.platform.Platform;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class QuestGroupService {
    private static QuestGroupProvider provider = new PersonalGroupProvider();

    private QuestGroupService() {
    }

    public static void initialize() {
        if (Platform.isModLoaded("ftbteams")) {
            provider = new FTBTeamsGroupProvider();
        }

        ParcelQuesting.LOGGER.info("Detected Mod FTB Teams, using as setting provide to {}", provider);
    }

    public static Optional<QuestGroupId> getGroupFor(ServerPlayer player) {
        QuestGroupProvider current = provider;

        return current.getGroupId(player).map(id -> new QuestGroupId(current.id(), id));
    }

    public static QuestGroupProvider getProvider() {
        return provider;
    }
}
