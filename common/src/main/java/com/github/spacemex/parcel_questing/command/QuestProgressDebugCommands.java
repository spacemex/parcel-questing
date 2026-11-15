package com.github.spacemex.parcel_questing.command;

import com.github.spacemex.parcel_questing.quest.QuestProgressStatus;
import com.github.spacemex.parcel_questing.quest.QuestService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class QuestProgressDebugCommands {

    private QuestProgressDebugCommands() {
    }

    public static void register() {
        CommandRegistrationEvent.EVENT.register(
                (dispatcher, registry, selection) -> {
                    dispatcher.register(
                            Commands.literal("pqprogress")
                                    .requires(
                                            Commands.hasPermission(
                                                    Commands.LEVEL_GAMEMASTERS
                                            )
                                    )
                                    .then(
                                            Commands.argument(
                                                            "requirement",
                                                            StringArgumentType.word()
                                                    )
                                                    .then(
                                                            Commands.argument(
                                                                            "count",
                                                                            LongArgumentType.longArg(0)
                                                                    )
                                                                    .then(
                                                                            Commands.argument(
                                                                                            "quest",
                                                                                            StringArgumentType.greedyString()
                                                                                    )
                                                                                    .executes(
                                                                                            QuestProgressDebugCommands::setProgress
                                                                                    )
                                                                    )
                                                    )
                                    )
                    );
                }
        );
    }

    private static int setProgress(
            CommandContext<CommandSourceStack> context
    ) throws CommandSyntaxException {
        ServerPlayer player =
                context.getSource().getPlayerOrException();

        String requirementId = StringArgumentType.getString(
                context,
                "requirement"
        );

        long count = LongArgumentType.getLong(
                context,
                "count"
        );

        String rawQuestId = StringArgumentType.getString(
                context,
                "quest"
        );

        Identifier questId;

        try {
            questId = Identifier.parse(rawQuestId);
        } catch (RuntimeException e) {
            context.getSource().sendFailure(
                    Component.literal("Invalid quest ID: " + rawQuestId)
            );

            return 0;
        }

        QuestProgressStatus result =
                QuestService.setRequirementCount(
                        player,
                        questId,
                        requirementId,
                        count
                );

        if (result != QuestProgressStatus.UPDATED) {
            context.getSource().sendFailure(
                    Component.literal("Progress update failed: " + result)
            );

            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal(
                        "Updated " + requirementId + " to " + count
                ),
                false
        );

        return Command.SINGLE_SUCCESS;
    }
}