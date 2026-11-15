package com.github.spacemex.parcel_questing.quest;

import com.github.spacemex.parcel_questing.quest.progress.GroupProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RewardPlan {

    private long experience;
    private long money;

    private final List<ItemStack> items = new ArrayList<>();

    public void addExperience(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Experience cannot be negative!");
        }

        experience = Math.addExact(experience, amount);
    }

    public void addMoney(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Money cannot be negative!");
        }

        money = Math.addExact(money, amount);
    }

    public void addItem(ItemStackTemplate template) {
        Objects.requireNonNull(template);

        ItemStack stack = template.create();

        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Item reward cannot be empty!");
        }

        items.add(stack);
    }

    public GroupProgress applyTo(GroupProgress group) {
        Objects.requireNonNull(group);

        long newBalance = Math.addExact(group.balance(), money);

        return group.addExperience(experience).withBalance(newBalance);
    }

    // Temporary Rewards Will be Moved to packages when implemented
    public void deliverTo(ServerPlayer player) {
        Objects.requireNonNull(player);

        for (ItemStack planned : items) {
            ItemStack stack = planned.copy();

            player.getInventory().add(stack);

            if (!stack.isEmpty()) {
                player.drop(stack, false);
            }
        }

        player.containerMenu.broadcastChanges();
    }
}
