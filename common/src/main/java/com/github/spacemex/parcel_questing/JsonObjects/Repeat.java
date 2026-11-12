package com.github.spacemex.parcel_questing.JsonObjects;

public record Repeat(boolean enabled, long cooldownTicks) {
    public Repeat {
        if (cooldownTicks < 0) {
            throw new IllegalStateException("cooldownTicks cannot be negative");
        }
    }
}
