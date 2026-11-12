package com.github.spacemex.parcel_questing.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public record Issuer(String name, Identifier skin) {
    public static final Codec<Issuer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(Issuer::name),
            Identifier.CODEC.fieldOf("skin").forGetter(Issuer::skin)
    ).apply(inst, Issuer::new));

    public Issuer {
        Objects.requireNonNull(name, "name");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }

    }
}
