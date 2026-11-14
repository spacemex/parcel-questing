package com.github.spacemex.parcel_questing.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public final class ContainerItem extends Item {
    private final int capacity;

    public ContainerItem(Properties properties, int capacity) {
        super(properties);

        if (capacity <= 0) {
            throw new IllegalArgumentException("Container item capacity must be greater than zero.");
        }

        this.capacity = capacity;
    }

    public int capacity() {
        return this.capacity;
    }

    public List<ItemStack> getContents(ItemStack containerStack) {
        NonNullList<ItemStack> contents = NonNullList.withSize(1, ItemStack.EMPTY);
        containerStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(contents);
        return contents.stream().filter(s -> !s.isEmpty()).map(ItemStack::copy).toList();
    }

    public int getContainedItemCount(ItemStack containerStack) {
        int count = 0;

        for (ItemStack stack : getContents(containerStack)) {
            count += stack.getCount();
        }

        return count;
    }

    //TODO Use -> Open Menu


    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }


    @Override
    @SuppressWarnings("all")
    @Deprecated
    // TODO Advanced Item Rendering
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        builder.accept(Component.translatable("tooltip.parcel_questing.container_item.items", getContainedItemCount(itemStack), capacity)
                .withStyle(ChatFormatting.GRAY));
        builder.accept(Component.translatable("tooltip.parcel_questing.container_item.open")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}

