package com.github.spacemex.parcel_questing.quest;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import com.github.spacemex.parcel_questing.api.requirement.Requirement;
import com.github.spacemex.parcel_questing.quest.group.QuestGroupId;
import com.github.spacemex.parcel_questing.quest.group.QuestGroupService;
import com.github.spacemex.parcel_questing.quest.progress.GroupProgress;
import com.github.spacemex.parcel_questing.quest.progress.QuestProgress;
import com.github.spacemex.parcel_questing.quest.storage.GroupProgressSavedData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Optional;

public final class QuestService {
    private QuestService() {
    }

    public static QuestAcceptStatus canAccept(Quest quest, GroupProgress group, long gameTick) {
        Objects.requireNonNull(quest);
        Objects.requireNonNull(group);

        if (!quest.forceQuest() && group.level() < quest.requiredLevel()) {
            return QuestAcceptStatus.LEVEL_TOO_LOW;
        }

        if (group.level() > quest.maxLevel()) {
            return QuestAcceptStatus.LEVEL_TOO_HIGH;
        }

        for (Identifier dependency : quest.depends()) {
            if (!group.hasCompleted(dependency)) {
                return QuestAcceptStatus.DEPENDENCY_INCOMPLETE;
            }
        }

        Optional<QuestProgress> existing = group.getQuest(quest.id());

        if (existing.isEmpty()) {
            return QuestAcceptStatus.ACCEPTED;
        }

        QuestProgress progress = existing.get();

        if (progress.active()) {
            return QuestAcceptStatus.ALREADY_ACTIVE;
        }

        if (!quest.repeat().enabled()) {
            return QuestAcceptStatus.ALREADY_COMPLETED;
        }

        long lastCompleted = progress.lastCompletedAtTick();

        if (lastCompleted >= 0) {
            long elapsed = gameTick - lastCompleted;

            if (elapsed < quest.repeat().cooldownTicks()) {
                return QuestAcceptStatus.COOLDOWN_ACTIVE;
            }
        }

        return QuestAcceptStatus.ACCEPTED;
    }

    public static QuestAcceptStatus acceptQuest(ServerPlayer player, Identifier questId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(questId);

        Optional<Quest> questOptional = ParcelQuesting.QUESTS.get(questId);

        if (questOptional.isEmpty()) {
            return QuestAcceptStatus.QUEST_NOT_FOUND;
        }

        Optional<QuestGroupId> groupOptional = QuestGroupService.getGroupFor(player);

        if (groupOptional.isEmpty()) {
            return QuestAcceptStatus.GROUP_NOT_FOUND;
        }

        Quest quest = questOptional.get();
        QuestGroupId groupId = groupOptional.get();

        GroupProgressSavedData savedData = GroupProgressSavedData.get(player.level().getServer());

        GroupProgress group = savedData.getGroup(groupId);

        long gameTick = player.level().getGameTime();

        QuestAcceptStatus status = canAccept(quest, group, gameTick);

        if (status != QuestAcceptStatus.ACCEPTED) {
            return status;
        }

        GroupProgress updated = group.acceptQuest(questId, gameTick);

        savedData.setGroup(groupId, updated);

        ParcelQuesting.LOGGER.debug("Group {} accepted quest {}", groupId, questId);

        return QuestAcceptStatus.ACCEPTED;
    }

    public static boolean isReadyToComplete(Quest quest, QuestProgress progress) {
        Objects.requireNonNull(quest);
        Objects.requireNonNull(progress);

        if (!progress.active()) {
            return false;
        }

        for (Requirement requirement : quest.requirements()) {
            long current = progress.getRequirementCount(requirement.progressKey());

            if (!requirement.isSatisfied(current)) {
                return false;
            }
        }

        return true;
    }

    public static QuestProgressStatus setRequirementCount(ServerPlayer player, Identifier questId, String requirementId, long count) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(questId);
        Objects.requireNonNull(requirementId);

        if (count < 0) {
            return QuestProgressStatus.INVALID_COUNT;
        }

        Optional<Quest> questOptional = ParcelQuesting.QUESTS.get(questId);

        if (questOptional.isEmpty()) {
            return QuestProgressStatus.QUEST_NOT_FOUND;
        }

        Quest quest = questOptional.get();

        Optional<Requirement> requirementOptional = quest.requirements().stream()
                .filter(requirement -> requirement.uniqueIdentifier().equals(requirementId)).findFirst();

        if (requirementOptional.isEmpty()) {
            return QuestProgressStatus.REQUIREMENT_NOT_FOUND;
        }

        Optional<QuestGroupId> groupOptional = QuestGroupService.getGroupFor(player);

        if (groupOptional.isEmpty()) {
            return QuestProgressStatus.GROUP_NOT_FOUND;
        }

        QuestGroupId groupId = groupOptional.get();
        GroupProgressSavedData savedData = GroupProgressSavedData.get(player.level().getServer());
        GroupProgress group = savedData.getGroup(groupId);
        Optional<QuestProgress> progressOptional = group.getQuest(questId);

        if (progressOptional.isEmpty()) {
            return QuestProgressStatus.QUEST_NOT_ACCEPTED;
        }

        QuestProgress progress = progressOptional.get();

        if (!progress.active()) {
            return QuestProgressStatus.QUEST_NOT_ACTIVE;
        }

        Requirement requirement = requirementOptional.get();
        GroupProgress updated = group.setRequirementCount(questId, requirement.progressKey(), count);

        savedData.setGroup(groupId, updated);

        return QuestProgressStatus.UPDATED;
    }
}
