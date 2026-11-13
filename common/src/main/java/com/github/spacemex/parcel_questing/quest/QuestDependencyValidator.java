package com.github.spacemex.parcel_questing.quest;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import net.minecraft.resources.Identifier;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public final class QuestDependencyValidator {

    private enum State {
        VISITING,
        VALID,
        INVALID
    }

    private QuestDependencyValidator() {
    }

    public static Map<Identifier, Quest> validate(Map<Identifier, Quest> quests) {
        Map<Identifier, State> states = new HashMap<>();

        for (Identifier id : quests.keySet()) {
            check(id, quests, states, new ArrayDeque<>());
        }

        Map<Identifier, Quest> validQuests = new HashMap<>();

        for (Map.Entry<Identifier, Quest> entry : quests.entrySet()) {
            if (states.get(entry.getKey()) == State.VALID) {
                validQuests.put(entry.getKey(), entry.getValue());
            }
        }

        return Map.copyOf(validQuests);
    }

    private static boolean check(Identifier id, Map<Identifier, Quest> quests, Map<Identifier, State> states, Deque<Identifier> path) {
        State state = states.get(id);

        if (state == State.VALID) {
            return true;
        }

        if (state == State.INVALID) {
            return false;
        }

        if (state == State.VISITING) {
            ParcelQuesting.LOGGER.warn("Circular quest dependency detected: {} -> {}", path, id);
        }

        Quest quest = quests.get(id);

        if (quest == null) {
            return false;
        }

        states.put(id, State.VISITING);
        path.addLast(id);

        boolean valid = true;

        for (Identifier dependency : quest.depends()) {
            if (!quests.containsKey(dependency)) {
                ParcelQuesting.LOGGER.warn("Quest {} depends on missing quest {}", id, dependency);
                valid = false;
                continue;
            }

            if (!check(dependency, quests, states, path)) {
                ParcelQuesting.LOGGER.warn("Quest {} has an invalid dependency {}", id, dependency);
                valid = false;
            }
        }

        path.removeLast();
        states.put(id, valid ? State.VALID : State.INVALID);

        return valid;
    }
}
