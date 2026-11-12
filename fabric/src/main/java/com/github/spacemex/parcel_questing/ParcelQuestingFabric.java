package com.github.spacemex.parcel_questing;

import net.fabricmc.api.ModInitializer;

public class ParcelQuestingFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ParcelQuesting.initialize();
    }
}
