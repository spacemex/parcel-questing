package com.github.spacemex.parcel_questing;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ParcelQuesting.MOD_ID)
public final class ParcelQuestingNeoForge {

    public ParcelQuestingNeoForge(IEventBus eventBus) {
        ParcelQuesting.initialize();
    }
}
