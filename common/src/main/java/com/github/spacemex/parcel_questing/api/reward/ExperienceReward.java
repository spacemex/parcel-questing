package com.github.spacemex.parcel_questing.api.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record ExperienceReward(long amount) implements BaseReward {
    public static final MapCodec<ExperienceReward> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.LONG.fieldOf("amount").forGetter(ExperienceReward::amount)
    ).apply(inst, ExperienceReward::new));

    public ExperienceReward {
        if (amount < 0) {
            throw new IllegalStateException("amount cannot be negative");
        }
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "group_experience");
    }
}
