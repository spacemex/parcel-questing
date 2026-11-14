package requirements;

import Utils.TestUtility;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class RequirementTests {
    private static final String ITEM_REQUIREMENT = "/item_requirement.json";
    private static final String FLUID_REQUIREMENT = "/fluid_requirement.json";
    private static final String BIOME_REQUIREMENT = "/biome_requirement.json";

    @Test
    void testItem() throws IOException {
        TestUtility.testRequirementDecode(TestUtility.loadJson(ITEM_REQUIREMENT), 2);
    }

    @Test
    void testFluid() throws IOException {
        TestUtility.testRequirementDecode(TestUtility.loadJson(FLUID_REQUIREMENT), 2);
    }

    @Test
    void testBiome() throws IOException {
        TestUtility.testRequirementDecode(TestUtility.loadJson(BIOME_REQUIREMENT), 2);
    }

    @Test
    void testItemRound() throws IOException {
        TestUtility.testRoundTrip(ITEM_REQUIREMENT);
    }

    @Test
    void testFluidRound() throws IOException {
        TestUtility.testRoundTrip(FLUID_REQUIREMENT);
    }

    @Test
    void testBiomeRound() throws IOException {
        TestUtility.testRoundTrip(BIOME_REQUIREMENT);
    }
}
