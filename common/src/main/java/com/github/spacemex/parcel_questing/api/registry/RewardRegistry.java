package com.github.spacemex.parcel_questing.api.registry;

import com.github.spacemex.parcel_questing.api.reward.BaseReward;
import com.github.spacemex.parcel_questing.api.reward.ExperienceReward;
import com.github.spacemex.parcel_questing.api.reward.ItemReward;
import com.github.spacemex.parcel_questing.api.reward.MoneyReward;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public final class RewardRegistry {
    private static final Map<Identifier, MapCodec<? extends BaseReward>> REWARDS = new HashMap<>();

    public static final Codec<BaseReward> CODEC = Identifier.CODEC.<BaseReward>partialDispatch("type",
            reward -> DataResult.success(reward.type()),
            id -> {
                MapCodec<? extends BaseReward> codec = REWARDS.get(id);

                if (codec == null) {
                    return DataResult.error(() -> "Unknow reward type: " + id);
                }

                return DataResult.success(codec);
            }
    );

    private RewardRegistry() {}

    public static <T extends BaseReward> void register(Identifier id, MapCodec<T> codec) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(codec, "codec");

        if (REWARDS.putIfAbsent(id, codec) != null) {
            throw new IllegalStateException("Duplicate reward type: " + id);
        }
    }

    static {
        register(Identifier.fromNamespaceAndPath(MOD_ID, "item"), ItemReward.MAP_CODEC);
        register(Identifier.fromNamespaceAndPath(MOD_ID, "money"), MoneyReward.MAP_CODEC);
        register(Identifier.fromNamespaceAndPath(MOD_ID, "group_experience"), ExperienceReward.MAP_CODEC);
    }
}
