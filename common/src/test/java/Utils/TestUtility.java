package Utils;

import com.github.spacemex.parcel_questing.ParcelQuesting;
import com.github.spacemex.parcel_questing.api.registry.RequirementRegistry;
import com.github.spacemex.parcel_questing.api.requirement.Requirement;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class TestUtility {
    private TestUtility() {
    }

    public static JsonElement loadJson(String path) throws IOException {
        try (InputStream stream = ParcelQuesting.class.getResourceAsStream(path)) {
            assertNotNull(stream, "Missing resource " + path);

            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(content);
        }
    }

    public static void testRoundTrip(String path) throws IOException {
        JsonElement json = loadJson(path);

        var codec = RequirementRegistry.CODEC.listOf();

        List<Requirement> requirements = new ArrayList<>();
        JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, requirements).getOrThrow();
        List<Requirement> decoded = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(requirements.size(), decoded.size());

        JsonElement re_encoded = codec.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow();
        assertEquals(encoded, re_encoded);
    }

    public static void testRequirementDecode(JsonElement loadedJson, int expectedElements) throws IOException {
        if (loadedJson == null) {
            throw new IllegalArgumentException("loadedJson is null");
        }

        if (expectedElements < 0) {
            throw new IllegalArgumentException("expectedElements cannot be negative");
        }

        List<Requirement> requirements = RequirementRegistry.CODEC.listOf().parse(JsonOps.INSTANCE, loadedJson).getOrThrow();

        assertNotNull(requirements);
        assertEquals(expectedElements, requirements.size());

        for (Requirement requirement : requirements) {
            assertNotNull(requirement.type());
            assertNotNull(requirement.progressKey());
            assertNotNull(requirement.uniqueIdentifier());
        }
    }
}
