package com.github.spacemex.parcel_questing.api.requirement;

import net.minecraft.resources.Identifier;

public interface Requirement {
    String uniqueIdentifier();

    Identifier type();

    String progressKey();
}
