package com.github.spacemex.parcel_questing.api.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public record MoneyReward(long amount) implements BaseReward {
    public static final MapCodec<MoneyReward> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.LONG.fieldOf("amount").forGetter(MoneyReward::amount)
    ).apply(inst, MoneyReward::new));

    public MoneyReward {
        if (amount < 0) {
            throw new IllegalStateException("amount cannot be negative");
        }
    }

    @Override
    public Identifier type() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "money");
    }
}
