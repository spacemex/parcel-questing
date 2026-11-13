package com.github.spacemex.parcel_questing.quest;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class QuestReloadListener extends SimpleJsonResourceReloadListener<Quest> {

    private volatile Map<Identifier, Quest> quests = Map.of();

    public QuestReloadListener() {
        super(Quest.CODEC, FileToIdConverter.json("parcel_questing/quests"));
    }

    @Override
    protected void apply(@NonNull Map<Identifier, Quest> preparations, @NonNull ResourceManager manager, @NonNull ProfilerFiller profiler) {
        Map<Identifier, Quest> loaded = new HashMap<>();

        for (Map.Entry<Identifier, Quest> entry : preparations.entrySet()) {
            Identifier fileId = entry.getKey();
            Quest quest = entry.getValue();

            if (!fileId.equals(quest.id())) {
                ParcelQuesting.LOGGER.warn("Skipping quest {}: JSON declares ID {}", fileId, quest.id());
                continue;
            }

            loaded.put(fileId, quest);
        }

        quests = Map.copyOf(loaded);

        ParcelQuesting.LOGGER.info("Loaded {} Parcel Questing quest", quests.size());
    }

    public Optional<Quest> get(Identifier id) {
        return Optional.ofNullable(quests.get(id));
    }

    public Map<Identifier, Quest> getAll() {
        return quests;
    }

    public int size() {
        return quests.size();
    }
}
