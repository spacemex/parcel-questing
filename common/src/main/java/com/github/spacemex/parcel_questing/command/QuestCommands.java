package com.github.spacemex.parcel_questing.command;

import com.github.spacemex.parcel_questing.quest.QuestAcceptStatus;
import com.github.spacemex.parcel_questing.quest.QuestService;
import com.github.spacemex.parcel_questing.quest.group.QuestGroupId;
import com.github.spacemex.parcel_questing.quest.group.QuestGroupService;
import com.github.spacemex.parcel_questing.quest.progress.QuestProgress;
import com.github.spacemex.parcel_questing.quest.storage.GroupProgressSavedData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class QuestCommands {

    private QuestCommands() {
    }

    public static void register() {
        CommandRegistrationEvent.EVENT.register(
                (dispatcher, registry, selection) -> {
                    dispatcher.register(
                            Commands.literal("pqquest")
                                    .then(
                                            Commands.literal("accept")
                                                    .then(
                                                            Commands.argument(
                                                                            "id",
                                                                            StringArgumentType.greedyString()
                                                                    )
                                                                    .executes(QuestCommands::accept)
                                                    )
                                    )
                                    .then(
                                            Commands.literal("status")
                                                    .then(
                                                            Commands.argument(
                                                                            "id",
                                                                            StringArgumentType.greedyString()
                                                                    )
                                                                    .executes(QuestCommands::status)
                                                    )
                                    )
                    );
                }
        );
    }

    private static int accept(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        Identifier id = parseId(context);

        if (id == null) {
            return 0;
        }

        QuestAcceptStatus result =
                QuestService.acceptQuest(player, id);

        if (result == QuestAcceptStatus.ACCEPTED) {
            context.getSource().sendSuccess(
                    () -> Component.literal("Accepted quest: " + id),
                    false
            );

            return Command.SINGLE_SUCCESS;
        }

        context.getSource().sendFailure(
                Component.literal("Cannot accept quest: " + result)
        );

        return 0;
    }

    private static int status(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();

        Identifier id = parseId(context);

        if (id == null) {
            return 0;
        }

        Optional<QuestGroupId> group =
                QuestGroupService.getGroupFor(player);

        if (group.isEmpty()) {
            context.getSource().sendFailure(
                    Component.literal("No group could be resolved")
            );

            return 0;
        }

        Optional<QuestProgress> progress =
                GroupProgressSavedData.get(
                                player.level().getServer()
                        )
                        .getGroup(group.get())
                        .getQuest(id);

        if (progress.isEmpty()) {
            context.getSource().sendSuccess(
                    () -> Component.literal("Quest not yet accepted: " + id),
                    false
            );

            return Command.SINGLE_SUCCESS;
        }

        QuestProgress quest = progress.get();

        context.getSource().sendSuccess(
                () -> Component.literal(
                        "Quest: " + id
                                + " | Active: " + quest.active()
                                + " | Completed: " + quest.completedCount()
                ),
                false
        );

        return Command.SINGLE_SUCCESS;
    }

    private static Identifier parseId(
            CommandContext<CommandSourceStack> context
    ) {
        String value = StringArgumentType.getString(context, "id");

        try {
            return Identifier.parse(value);
        } catch (RuntimeException e) {
            context.getSource().sendFailure(
                    Component.literal("Invalid quest ID: " + value)
            );

            return null;
        }
    }
}