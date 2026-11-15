package com.github.spacemex.parcel_questing.api.registry;

import com.github.spacemex.parcel_questing.api.reward.BaseReward;
import com.github.spacemex.parcel_questing.api.reward.ExperienceReward;
import com.github.spacemex.parcel_questing.api.reward.ItemReward;
import com.github.spacemex.parcel_questing.api.reward.MoneyReward;
import com.github.spacemex.parcel_questing.quest.RewardPlan;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.github.spacemex.parcel_questing.ParcelQuesting.MOD_ID;

public final class RewardRegistry {
    private static final Map<Identifier, MapCodec<? extends BaseReward>> REWARDS = new HashMap<>();
    private static final Map<Identifier, BiConsumer<BaseReward, RewardPlan>> HANDLERS = new HashMap<>();

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

    public static <T extends BaseReward> void register(Identifier id, Class<T> rewardClass, MapCodec<T> codec, BiConsumer<T, RewardPlan> handler) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(rewardClass, "rewardClass");
        Objects.requireNonNull(codec, "codec");
        Objects.requireNonNull(handler, "handler");

        if (REWARDS.putIfAbsent(id, codec) != null) {
            throw new IllegalStateException("Duplicate reward type: " + id);
        }

        HANDLERS.putIfAbsent(id, (r, p) -> handler.accept(rewardClass.cast(r), p));
    }

    public static void planReward(BaseReward reward, RewardPlan plan) {
        Objects.requireNonNull(reward);
        Objects.requireNonNull(plan);

        BiConsumer<BaseReward, RewardPlan> handler = HANDLERS.get(reward.type());

        if (handler == null) {
            throw new IllegalStateException("No handler registered for reward: " + reward.type());
        }

        handler.accept(reward, plan);
    }

    static {
        register(Identifier.fromNamespaceAndPath(MOD_ID, "item"), ItemReward.class, ItemReward.MAP_CODEC, (r, p) -> p.addItem(r.stack()));
        register(Identifier.fromNamespaceAndPath(MOD_ID, "money"), MoneyReward.class, MoneyReward.MAP_CODEC, (r, p) -> p.addMoney(r.amount()));
        register(Identifier.fromNamespaceAndPath(MOD_ID, "group_experience"), ExperienceReward.class, ExperienceReward.MAP_CODEC, (r, p) -> p.addExperience(r.amount()));
    }
}
