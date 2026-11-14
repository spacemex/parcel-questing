package com.github.spacemex.parcel_questing;

import com.github.spacemex.parcel_questing.command.QuestDebugCommands;
import com.github.spacemex.parcel_questing.quest.QuestReloadListener;
import com.github.spacemex.parcel_questing.quest.group.QuestGroupService;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ParcelQuesting {
    public static final String MOD_ID = "parcel_questing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final QuestReloadListener QUESTS = new QuestReloadListener();

    public static void initialize() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, QUESTS, Identifier.fromNamespaceAndPath(MOD_ID, "quests"));

        QuestGroupService.initialize();
        QuestDebugCommands.register();

        LOGGER.info("Parcel Questing initialized!");
    }

    public static void initializeClientOnly() {
        LOGGER.info("Hello Client!");
    }
}
