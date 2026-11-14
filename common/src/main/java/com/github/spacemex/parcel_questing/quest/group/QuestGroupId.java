package com.github.spacemex.parcel_questing.quest.group;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.UUID;

public record QuestGroupId(Identifier provider, UUID id) {
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(value -> {
        try {
            return DataResult.success(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Invalid group UUID: " + value);
        }
    }, UUID::toString);

    public static final Codec<QuestGroupId> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("provider").forGetter(QuestGroupId::provider),
            UUID_CODEC.fieldOf("id").forGetter(QuestGroupId::id)
    ).apply(inst, QuestGroupId::new));

    public QuestGroupId {
        Objects.requireNonNull(provider, "provider");
        Objects.requireNonNull(id, "id");
    }
}
