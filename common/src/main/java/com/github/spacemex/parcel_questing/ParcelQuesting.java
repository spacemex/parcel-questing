package com.github.spacemex.parcel_questing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ParcelQuesting {
    public static final String MOD_ID = "parcel_questing";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void initialize() {
        LOGGER.info("Hello World!");
    }

    public static void initializeClientOnly() {
        LOGGER.info("Hello Client!");
    }
}
