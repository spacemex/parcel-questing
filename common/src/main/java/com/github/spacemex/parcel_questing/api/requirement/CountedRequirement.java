package com.github.spacemex.parcel_questing.api.requirement;

public interface CountedRequirement extends Requirement {
    long amount();

    @Override
    default boolean isSatisfied(long progress) {
        return progress >= amount();
    }
}
