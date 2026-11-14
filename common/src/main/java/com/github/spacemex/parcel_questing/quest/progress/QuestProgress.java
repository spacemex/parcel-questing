package com.github.spacemex.parcel_questing.quest.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record QuestProgress(boolean active, int completedCount, long startedAtTick, long lastCompletedAtTick,
                            Map<String, Long> requirementCounts) {
    public static final Codec<QuestProgress> CODEC = RecordCodecBuilder.<QuestProgress>create(inst -> inst.group(
            Codec.BOOL.fieldOf("active").forGetter(QuestProgress::active),
            Codec.INT.fieldOf("completedCount").forGetter(QuestProgress::completedCount),
            Codec.LONG.fieldOf("startedAtTick").forGetter(QuestProgress::startedAtTick),
            Codec.LONG.fieldOf("lastCompletedAtTick").forGetter(QuestProgress::lastCompletedAtTick),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("requirements").forGetter(QuestProgress::requirementCounts)
    ).apply(inst, QuestProgress::new)).flatXmap(QuestProgress::validate, DataResult::success);

    public QuestProgress {
        requirementCounts = Map.copyOf(requirementCounts);
    }

    private static DataResult<QuestProgress> validate(QuestProgress progress) {
        if (progress.completedCount < 0) {
            return DataResult.error(() -> "Completion count cannot be negative");
        }

        if (progress.startedAtTick < 0 || progress.lastCompletedAtTick < -1) {
            return DataResult.error(() -> "Invalid quest timestamps");
        }

        for (long count : progress.requirementCounts.values()) {
            if (count < 0) {
                return DataResult.error(() -> "Requirement progress cannot be negative");
            }
        }

        return DataResult.success(progress);
    }

    public static QuestProgress begin(long gameTick) {
        if (gameTick < 0) {
            throw new IllegalArgumentException("Game tick cannot be negative");
        }

        return new QuestProgress(true, 0, gameTick, -1, Map.of());
    }

    public QuestProgress setRequirementCount(String requirementId, long count) {
        Objects.requireNonNull(requirementId);

        if (!active) {
            throw new IllegalArgumentException("Quest is not active");
        }

        if (requirementId.isBlank() || count < 0) {
            throw new IllegalArgumentException("Invalid requirement progress");
        }

        Map<String, Long> updated = new HashMap<>(requirementCounts);
        updated.put(requirementId, count);

        return new QuestProgress(active, completedCount, startedAtTick, lastCompletedAtTick, updated);
    }


    public QuestProgress complete(long gameTick) {
        if (!active) {
            throw new IllegalArgumentException("Quest is not active");
        }

        if (gameTick < startedAtTick) {
            throw new IllegalArgumentException("Invalid completion time");
        }

        return new QuestProgress(false, Math.addExact(completedCount, 1), startedAtTick, gameTick, requirementCounts);
    }

    public QuestProgress startNewAttempt(long gameTick) {
        if (active) {
            throw new IllegalArgumentException("Quest is already active");
        }

        if (gameTick < 0) {
            throw new IllegalArgumentException("Game tick cannot be negative");
        }

        return new QuestProgress(true, completedCount, gameTick, lastCompletedAtTick, Map.of());
    }

    public long getRequirementCount(String requirementId) {
        return requirementCounts.getOrDefault(requirementId, 0L);
    }
}
