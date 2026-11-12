package com.github.spacemex.parcel_questing.JsonObjects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Repeat(boolean enabled, long cooldownTicks) {
    public static final Codec<Repeat> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("enabled").forGetter(Repeat::enabled),
            Codec.LONG.fieldOf("cooldownTicks").forGetter(Repeat::cooldownTicks)
    ).apply(inst, Repeat::new));

    public Repeat {
        if (cooldownTicks < 0) {
            throw new IllegalStateException("cooldownTicks cannot be negative");
        }
    }
}
