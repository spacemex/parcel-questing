package com.github.spacemex.parcel_questing.command;

import com.github.spacemex.parcel_questing.quest.group.QuestGroupId;
import com.github.spacemex.parcel_questing.quest.group.QuestGroupService;
import com.github.spacemex.parcel_questing.quest.progress.GroupProgress;
import com.github.spacemex.parcel_questing.quest.storage.GroupProgressSavedData;
import com.mojang.brigadier.arguments.LongArgumentType;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class QuestDebugCommands {

    private QuestDebugCommands() {
    }

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
                    dispatcher.register(
                            Commands.literal("pqdebug")
                                    .requires(
                                            Commands.hasPermission(
                                                    Commands.LEVEL_GAMEMASTERS)).then(Commands.literal("balance")
                                            .then(Commands.literal("get").executes(context -> {
                                                        ServerPlayer player = context.getSource().getPlayerOrException();
                                                        QuestGroupId groupId = QuestGroupService.getGroupFor(player).orElse(null);

                                                        if (groupId == null) {
                                                            context.getSource().sendFailure(Component.literal("No group could be resolved"));
                                                            return 0;
                                                        }

                                                        GroupProgress progress = GroupProgressSavedData.get(player.level().getServer()).getGroup(groupId);
                                                        context.getSource().sendSuccess(() -> Component.literal("Group balance: " + progress.balance()), false);
                                                        return 1;
                                                    })
                                            ).then(Commands.literal("add")
                                                    .then(Commands.argument("amount", LongArgumentType.longArg(1)).executes(context -> {
                                                        ServerPlayer player = context.getSource().getPlayerOrException();
                                                        QuestGroupId groupId = QuestGroupService.getGroupFor(player).orElse(null);

                                                        if (groupId == null) {
                                                            context.getSource().sendFailure(Component.literal("No group could be resolved"));
                                                            return 0;
                                                        }

                                                        long amount = LongArgumentType.getLong(context, "amount");
                                                        GroupProgress updated = GroupProgressSavedData.get(player.level().getServer()).updateGroup(groupId, progress -> progress.withBalance(Math.addExact(progress.balance(), amount)));

                                                        context.getSource().sendSuccess(() -> Component.literal("New group balance: " + updated.balance()), false);
                                                        return 1;
                                                    })))));
                }
        );
    }
}