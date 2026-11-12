package com.github.spacemex.parcel_questing.api.registry;

import com.github.spacemex.parcel_questing.api.requirement.BiomeRequirement;
import com.github.spacemex.parcel_questing.api.requirement.FluidRequirement;
import com.github.spacemex.parcel_questing.api.requirement.ItemRequirement;
import com.github.spacemex.parcel_questing.api.requirement.Requirement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public final class RequirementRegistry {
    private static final Map<Identifier, MapCodec<? extends Requirement>> REQUIREMENTS = new HashMap<>();

    public static final Codec<Requirement> CODEC = Identifier.CODEC.<Requirement>partialDispatch("type", requirement -> DataResult.success(requirement.type()),
            id -> {
                MapCodec<? extends Requirement> codec = REQUIREMENTS.get(id);

                if (codec == null) {
                    return DataResult.error(() -> "Unknow requirement type: " + id);
                }

                return DataResult.success(codec);
            });

    private RequirementRegistry() {
    }

    public static <T extends Requirement> void register(Identifier id, MapCodec<T> codec) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(codec);

        if (REQUIREMENTS.putIfAbsent(id, codec) != null) {
            throw new IllegalStateException("Duplicate requirement id: " + id);
        }
    }

    static {
        register(Identifier.fromNamespaceAndPath(MOD_ID, "item"), ItemRequirement.MAP_CODEC);
        register(Identifier.fromNamespaceAndPath(MOD_ID, "fluid"), FluidRequirement.MAP_CODEC);
        register(Identifier.fromNamespaceAndPath(MOD_ID, "biome"), BiomeRequirement.MAP_CODEC);
    }
}
