package com.github.spacemex.parcel_questing.quest.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record GroupProgress(int level, long totalExperience, long balance, Map<Identifier, QuestProgress> quests) {
    public static final Codec<GroupProgress> CODEC = RecordCodecBuilder.<GroupProgress>create(inst -> inst.group(
            Codec.INT.fieldOf("level").forGetter(GroupProgress::level),
            Codec.LONG.fieldOf("totalExperience").forGetter(GroupProgress::totalExperience),
            Codec.LONG.fieldOf("balance").forGetter(GroupProgress::balance),
            Codec.unboundedMap(Identifier.CODEC, QuestProgress.CODEC).fieldOf("quests").forGetter(GroupProgress::quests)
    ).apply(inst, GroupProgress::new)).flatXmap(GroupProgress::validate, DataResult::success);

    public GroupProgress {
        quests = Map.copyOf(quests);
    }

    private static DataResult<GroupProgress> validate(GroupProgress progress) {
        if (progress.level < 1) {
            return DataResult.error(() -> "Group level must be at least 1");
        }

        if (progress.totalExperience < 0) {
            return DataResult.error(() -> "Total experience cannot be negative");
        }

        if (progress.balance < 0) {
            return DataResult.error(() -> "Balance cannot be negative");
        }

        return DataResult.success(progress);
    }

    public static GroupProgress empty() {
        return new GroupProgress(1, 0L, 0L, Map.of());
    }

    public Optional<QuestProgress> getQuest(Identifier questId) {
        return Optional.ofNullable(quests.get(questId));
    }

    public boolean hasCompleted(Identifier questId) {
        return getQuest(questId).map(progress -> progress.completedCount() > 0).orElse(false);
    }

    public GroupProgress acceptQuest(Identifier questId, long gameTick) {
        QuestProgress current = quests.get(questId);

        QuestProgress updated = current == null ? QuestProgress.begin(gameTick) : current.startNewAttempt(gameTick);
        return withQuest(questId, updated);
    }

    public GroupProgress setRequirementCount(Identifier questId, String requirementId, long count) {
        QuestProgress current = getQuest(questId).orElseThrow(() -> new IllegalStateException("Quest is not tracked: " + questId));
        return withQuest(questId, current.setRequirementCount(requirementId, count));
    }

    public GroupProgress completeQuest(Identifier questId, long gameTick) {
        QuestProgress current = getQuest(questId).orElseThrow(() -> new IllegalStateException("Quest is not tracked: " + questId));
        return withQuest(questId, current.complete(gameTick));
    }

    public GroupProgress addExperience(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }

        return new GroupProgress(level, Math.addExact(totalExperience, amount), balance, quests);
    }

    public GroupProgress withLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Group level must be at least 1");
        }

        return new GroupProgress(level, totalExperience, balance, quests);
    }

    public GroupProgress withBalance(long balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        return new GroupProgress(level, totalExperience, balance, quests);
    }

    public boolean hasBalance(long amount) {
        return amount >= 0 && balance >= amount;
    }

    public GroupProgress withQuest(Identifier questId, QuestProgress progress) {
        Map<Identifier, QuestProgress> updated = new HashMap<>(quests);
        updated.put(questId, progress);

        return new GroupProgress(level, totalExperience, balance, updated);
    }
}
