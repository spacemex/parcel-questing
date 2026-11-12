package com.github.spacemex.parcel_questing.client;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = ParcelQuesting.MOD_ID, dist = Dist.CLIENT)
public final class ParcelQuestingNeoForgeClient {

    public ParcelQuestingNeoForgeClient(IEventBus eventBus) {
        eventBus.addListener(this::clientInitialization);
    }

    private void clientInitialization(FMLClientSetupEvent event) {
        ParcelQuesting.initializeClientOnly();
    }
}
