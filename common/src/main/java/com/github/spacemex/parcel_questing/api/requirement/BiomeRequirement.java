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

public record BiomeRequirement(String uniqueId, TargetType targetType, Identifier target) implements Requirement {
    private record Fields(String id, Optional<Identifier> biome, Optional<Identifier> tag) {
    }

    private static final MapCodec<Fields> FIELDS_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("id").forGetter(Fields::id),
            Identifier.CODEC.optionalFieldOf("biome").forGetter(Fields::biome),
            Identifier.CODEC.optionalFieldOf("tag").forGetter(Fields::tag)
    ).apply(inst, Fields::new));

    public static final MapCodec<BiomeRequirement> MAP_CODEC = FIELDS_CODEC.flatXmap(fields -> {
        if (fields.id().isBlank()) {
            return DataResult.error(() -> "Requirement ID cannot be blank");
        }

        if (fields.biome().isPresent() == fields.tag().isPresent()) {
            return DataResult.error(() -> "Exactly one of `biome` of `tag` is required");
        }

        BiomeRequirement requirement = fields.biome().isPresent() ? BiomeRequirement.biome(fields.id(), fields.biome().orElseThrow()) :
                BiomeRequirement.tag(fields.id(), fields.tag().orElseThrow());

        return DataResult.success(requirement);
    }, requirement -> DataResult.success(new Fields(requirement.uniqueId(), requirement.isBiome() ? Optional.of(requirement.target()) :
            Optional.empty(), requirement.isTag() ? Optional.of(requirement.target()) : Optional.empty())));

    public BiomeRequirement {
        Objects.requireNonNull(target);
        Objects.requireNonNull(targetType);
        Objects.requireNonNull(uniqueId);

        if (uniqueId.isBlank()) {
            throw new IllegalArgumentException("Requirement ID cannot be blank");
        }

        if (targetType != TargetType.BIOME && targetType != TargetType.TAG) {
            throw new IllegalArgumentException("Target type must be either BIOME or TAG");
        }
    }

    public static BiomeRequirement biome(String uniqueId, Identifier biome) {
        return new BiomeRequirement(uniqueId, TargetType.BIOME, biome);
    }

    public static BiomeRequirement tag(String uniqueId, Identifier tag) {
        return new BiomeRequirement(uniqueId, TargetType.TAG, tag);
    }

    public boolean isBiome() {
        return targetType == TargetType.BIOME;
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
        return Identifier.fromNamespaceAndPath(MOD_ID, "biome");
    }

    @Override
    public String progressKey() {
        return uniqueIdentifier();
    }
}