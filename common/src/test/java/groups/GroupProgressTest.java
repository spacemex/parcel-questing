package groups;

import com.github.spacemex.parcel_questing.quest.progress.GroupProgress;
import com.github.spacemex.parcel_questing.quest.progress.QuestProgress;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class GroupProgressTest {

    @Test
    void testQuestProgressRoundTrip() {
        Identifier questId = Identifier.parse("parcel_questing:getting_started");

        GroupProgress group = GroupProgress.empty()
                .acceptQuest(questId, 100)
                .setRequirementCount(questId, "collect_logs", 16)
                .setRequirementCount(questId, "collect_stone_axe", 1)
                .completeQuest(questId, 200)
                .addExperience(10)
                .withBalance(100);

        assertTrue(group.hasCompleted(questId));
        assertEquals(10L, group.totalExperience());
        assertEquals(100L, group.balance());

        QuestProgress quest = group.getQuest(questId).orElseThrow();

        assertFalse(quest.active());
        assertEquals(1, quest.completedCount());
        assertEquals(16L, quest.getRequirementCount("collect_logs"));

        JsonElement encoded = GroupProgress.CODEC.encodeStart(JsonOps.INSTANCE, group).getOrThrow();

        GroupProgress decoded = GroupProgress.CODEC.parse(JsonOps.INSTANCE, encoded).getOrThrow();
        assertEquals(group, decoded);
    }

    @Test
    void testRepeatableQuestPreservesCompletionHistory() {
        Identifier questId = Identifier.parse("parcel_questing:getting_started");

        GroupProgress group = GroupProgress.empty()
                .acceptQuest(questId, 100)
                .setRequirementCount(questId, "collect_logs", 16)
                .completeQuest(questId, 200);

        GroupProgress repeated = group.acceptQuest(questId, 72000);

        QuestProgress progress = repeated.getQuest(questId).orElseThrow();

        assertTrue(progress.active());
        assertEquals(1, progress.completedCount());
        assertEquals(0L, progress.getRequirementCount("collect_logs"));
        assertEquals(200L, progress.lastCompletedAtTick());

        assertTrue(repeated.hasCompleted(questId));
    }
}
