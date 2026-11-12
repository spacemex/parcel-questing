package com.github.spacemex.parcel_questing.api.requirements;

import net.minecraft.resources.Identifier;

public interface Requirement {
    String uniqueIdentifier();

    Identifier type();

    String progressKey();
}
