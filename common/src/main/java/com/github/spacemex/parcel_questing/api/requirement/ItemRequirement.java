package com.github.spacemex.parcel_questing.api.requirement;

import com.github.spacemex.parcel_questing.api.TargetType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.Optional;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record ItemRequirement(String uniqueId, TargetType targetType, Identifier target,
                              long amount) implements CountedRequirement {
    private record Fields(String id, Optional<Identifier> item, Optional<Identifier> tag, long amount) {}

    private static final MapCodec<Fields> FIELDS_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("id").forGetter(Fields::id),
            Identifier.CODEC.optionalFieldOf("item").forGetter(Fields::item),
            Identifier.CODEC.optionalFieldOf("tag").forGetter(Fields::tag),
            Codec.LONG.fieldOf("amount").forGetter(Fields::amount)
    ).apply(inst, Fields::new));

    public static final MapCodec<ItemRequirement> MAP_CODEC = FIELDS_CODEC.flatXmap(fields -> {
        if (fields.id().isBlank()) {
            return DataResult.error(() -> "Requirement ID cannot be blank");
        }

        if (fields.amount() <= 0) {
            return DataResult.error(() -> "Item amount must be greater than zero");
        }

        if (fields.item().isPresent() == fields.tag().isPresent()) {
            return DataResult.error(() -> "Exactly one of `item` or `tag` is required");
        }

        ItemRequirement requirement = fields.item().isPresent() ? ItemRequirement.item(fields.id(), fields.item().orElseThrow(), fields.amount()) :
                ItemRequirement.tag(fields.id(), fields.tag().orElseThrow(), fields.amount());

        return DataResult.success(requirement);
    }, requirement -> DataResult.success(new Fields(requirement.uniqueIdentifier(), requirement.isItem() ?
            Optional.of(requirement.target()) : Optional.empty(), requirement.isTag() ? Optional.of(requirement.target()) : Optional.empty(), requirement.amount())));


    public ItemRequirement {
        Objects.requireNonNull(targetType, "targetType");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(uniqueId, "uniqueId");

        if (uniqueId.isBlank()) {
            throw new IllegalArgumentException("Requirement ID cannot be blank");
        }

        if (targetType != TargetType.ITEM && targetType != TargetType.TAG) {
            throw new IllegalArgumentException("targetType must be either ITEM or TAG");
        }

        if (amount <= 0L) {
            throw new IllegalArgumentException("Item requirement amount must be greater than zero.");
        }
    }

    public static ItemRequirement item(String uniqueId, Identifier item, long amount) {
        return new ItemRequirement(uniqueId, TargetType.ITEM, item, amount);
    }

    public static ItemRequirement tag(String uniqueId, Identifier tag, long amount) {
        return new ItemRequirement(uniqueId, TargetType.TAG, tag, amount);
    }

    public boolean isItem() {
        return targetType == TargetType.ITEM;
    }

    public boolean isTag() {
        return targetType == TargetType.TAG;
    }

    @Override
    public String uniqueIdentifier() {
        return uniqueId;
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "item");
    }

    @Override
    public String progressKey() {
        return uniqueIdentifier();
    }
}
