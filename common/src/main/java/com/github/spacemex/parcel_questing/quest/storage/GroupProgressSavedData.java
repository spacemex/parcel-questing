package com.github.spacemex.parcel_questing.quest.storage;

import com.github.spacemex.parcel_questing.quest.group.QuestGroupId;
import com.github.spacemex.parcel_questing.quest.progress.GroupProgress;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public final class GroupProgressSavedData extends SavedData {
    private record Entry(QuestGroupId groupId, GroupProgress progress) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                QuestGroupId.CODEC.fieldOf("group").forGetter(Entry::groupId),
                GroupProgress.CODEC.fieldOf("progress").forGetter(Entry::progress)
        ).apply(inst, Entry::new));
    }

    private static final Codec<GroupProgressSavedData> CODEC = Entry.CODEC.listOf().fieldOf("groups").codec()
            .comapFlatMap(GroupProgressSavedData::decode, GroupProgressSavedData::encode);
    public static final SavedDataType<GroupProgressSavedData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(MOD_ID, "group_progress"),
            GroupProgressSavedData::new, CODEC, null);

    private final Map<QuestGroupId, GroupProgress> groups;

    public GroupProgressSavedData() {
        this.groups = new HashMap<>();
    }

    private GroupProgressSavedData(Map<QuestGroupId, GroupProgress> groups) {
        this.groups = groups;
    }

    private static DataResult<GroupProgressSavedData> decode(List<Entry> entries) {
        Map<QuestGroupId, GroupProgress> decoded = new HashMap<>();

        for (Entry entry : entries) {
            if (decoded.putIfAbsent(entry.groupId, entry.progress) != null) {
                return DataResult.error(() -> "Duplicate group ID: " + entry.groupId);
            }
        }

        return DataResult.success(new GroupProgressSavedData(decoded));
    }

    private List<Entry> encode() {
        return groups.entrySet().stream().map(entry -> new Entry(entry.getKey(), entry.getValue())).toList();
    }

    public static GroupProgressSavedData get(MinecraftServer server) {
        return server.getDataStorage().computeIfAbsent(TYPE);
    }

    public GroupProgress getGroup(QuestGroupId groupId) {
        return groups.getOrDefault(groupId, GroupProgress.empty());
    }

    public void setGroup(QuestGroupId groupId, GroupProgress progress) {
        Objects.requireNonNull(groupId);
        Objects.requireNonNull(progress);

        GroupProgress previous = groups.get(groupId);

        if (progress.equals(previous)) {
            return;
        }

        groups.put(groupId, progress);
        setDirty();
    }

    public GroupProgress updateGroup(QuestGroupId groupId, UnaryOperator<GroupProgress> action) {
        GroupProgress current = getGroup(groupId);
        GroupProgress updated = Objects.requireNonNull(action.apply(current), "Updated group progress cannot be null");

        setGroup(groupId, updated);
        return updated;
    }

    public int getGroupCount() {
        return groups.size();
    }
}
