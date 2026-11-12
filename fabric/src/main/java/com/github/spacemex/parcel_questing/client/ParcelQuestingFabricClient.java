package com.github.spacemex.parcel_questing.client;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import net.fabricmc.api.ClientModInitializer;

public class ParcelQuestingFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParcelQuesting.initializeClientOnly();
    }
}
