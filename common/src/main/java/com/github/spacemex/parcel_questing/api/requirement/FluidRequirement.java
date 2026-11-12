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

public record FluidRequirement(String uniqueId, TargetType targetType, Identifier target,
                               long amount) implements CountedRequirement {
    private record Fields(String id, Optional<Identifier> fluid, Optional<Identifier> tag, long amount) {}

    private static final MapCodec<Fields> FIELDS_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("id").forGetter(Fields::id),
            Identifier.CODEC.optionalFieldOf("fluid").forGetter(Fields::fluid),
            Identifier.CODEC.optionalFieldOf("tag").forGetter(Fields::tag),
            Codec.LONG.fieldOf("amount").forGetter(Fields::amount)
    ).apply(inst, Fields::new));

    public static final MapCodec<FluidRequirement> MAP_CODEC = FIELDS_CODEC.flatXmap(fields -> {
        if (fields.id.isBlank()) {
            return DataResult.error(() -> "Requirement ID cannot be blank");
        }

        if (fields.amount() <= 0) {
            return DataResult.error(() -> "Fluid amount cannot be zero");
        }

        if (fields.fluid().isPresent() == fields.tag().isPresent()) {
            return DataResult.error(() -> "Exactly one of `fluid` or `tag` is required");
        }

        FluidRequirement requirement = fields.fluid().isPresent() ? FluidRequirement.fluid(fields.id(), fields.fluid().orElseThrow(), fields.amount()) :
                FluidRequirement.tag(fields.id(), fields.tag().orElseThrow(), fields.amount());

        return DataResult.success(requirement);
    }, requirement -> DataResult.success(new Fields(
            requirement.uniqueIdentifier(),
            requirement.isFluid() ? Optional.of(requirement.target()) : Optional.empty(),
            requirement.isTag() ? Optional.of(requirement.target()) : Optional.empty(), requirement.amount()
    )));

    public FluidRequirement {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(targetType, "targetType");

        if (targetType != TargetType.FLUID && targetType != TargetType.TAG) {
            throw new IllegalArgumentException("targetType must be either FLUID or TAG");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Fluid requirement amount must be greater than zero.");
        }
    }

    public static FluidRequirement fluid(String uniqueId, Identifier fluid, long amount) {
        return new FluidRequirement(uniqueId, TargetType.FLUID, fluid, amount);
    }

    public static FluidRequirement tag(String uniqueId, Identifier tag, long amount) {
        return new FluidRequirement(uniqueId, TargetType.TAG, tag, amount);
    }

    public boolean isFluid() {
        return targetType == TargetType.FLUID;
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
        return Identifier.fromNamespaceAndPath(MOD_ID, "fluid");
    }

    @Override
    public String progressKey() {
        return uniqueIdentifier();
    }
}
