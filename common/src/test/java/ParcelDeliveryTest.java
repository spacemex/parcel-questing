import com.github.spacemex.parcel_questing.api.registry.RequirementRegistry;
import com.github.spacemex.parcel_questing.api.requirement.Requirement;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParcelDeliveryTest {
    private static final String ITEM_REQUIREMENT_PATH = "/item_requirement.json";
    private static final String FLUID_REQUIREMENT_PATH = "/fluid_requirement.json";
    private static final String BIOME_REQUIREMENT_PATH = "/biome_requirement.json";

    @Test
    void testItemRequirementDecoding() throws IOException {
        testRequirementDecoding(loadJson(ITEM_REQUIREMENT_PATH), 2);
    }

    @Test
    void testItemRequirementRoundTrip() throws IOException {
        testRequirementRoundTrip(loadJson(ITEM_REQUIREMENT_PATH));
    }

    @Test
    void testFluidRequirementDecoding() throws IOException {
        testRequirementDecoding(loadJson(FLUID_REQUIREMENT_PATH), 2);
    }

    @Test
    void testFluidRequirementRoundTrip() throws IOException {
        testRequirementRoundTrip(loadJson(FLUID_REQUIREMENT_PATH));
    }

    @Test
    void testBiomeRequirementDecoding() throws IOException {
        testRequirementDecoding(loadJson(BIOME_REQUIREMENT_PATH), 2);
    }

    @Test
    void testBiomeRequirementRoundTrip() throws IOException {
        testRequirementRoundTrip(loadJson(BIOME_REQUIREMENT_PATH));
    }

    private static void testRequirementRoundTrip(JsonElement loadedJson) throws IOException {
        if (loadedJson == null) {
            throw new IllegalArgumentException("loadedJson is null");
        }

        var codec = RequirementRegistry.CODEC.listOf();

        List<Requirement> requirements = codec.parse(JsonOps.INSTANCE, loadedJson).getOrThrow();

        JsonElement encoded = codec.encodeStart(JsonOps.INSTANCE, requirements).getOrThrow();

        List<Requirement> decoded = codec.parse(JsonOps.INSTANCE, encoded).getOrThrow();

        assertEquals(requirements.size(), decoded.size());

        JsonElement reencoded = codec.encodeStart(JsonOps.INSTANCE, decoded).getOrThrow();
        assertEquals(encoded, reencoded);
    }

    private static void testRequirementDecoding(JsonElement loadedJson, int elementsExpected) throws IOException {
        if (loadedJson == null) {
            throw new IllegalArgumentException("loadedJson is null");
        }

        if (elementsExpected <= 0) {
            throw new IllegalArgumentException("elementsExpected > 0");
        }

        List<Requirement> requirements = RequirementRegistry.CODEC.listOf().parse(JsonOps.INSTANCE, loadedJson).getOrThrow();

        assertNotNull(requirements);
        assertEquals(elementsExpected, requirements.size());

        for (Requirement requirement : requirements) {
            assertNotNull(requirement.type());
            assertNotNull(requirement.progressKey());
        }
    }

    private static JsonElement loadJson(String path) throws IOException {
        try (InputStream stream = ParcelDeliveryTest.class.getResourceAsStream(path)) {

            assertNotNull(stream, "Missing resource " + path);

            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(content);
        }
    }
}
