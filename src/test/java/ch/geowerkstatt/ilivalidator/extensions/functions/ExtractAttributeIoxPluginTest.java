package ch.geowerkstatt.ilivalidator.extensions.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ExtractAttributeIoxPluginTest {
    private static final String TEST_DATA = "ExtractAttribute/TestData.xtf";
    private ValidationTestHelper vh;

    @BeforeEach
    void setUp() {
        vh = new ValidationTestHelper();
        vh.addFunction(new FindObjectsIoxPlugin());
        vh.addFunction(new ExtractAttributeIoxPlugin());
        vh.addFunction(new ElementCountIoxPlugin());
    }

    @Test
    void extractAttribute() throws Exception {
        vh.runValidation(new String[]{TEST_DATA}, new String[]{"ExtractAttribute/ExtractAttribute.ili"});
        assertEquals(0, vh.getErrs().size());
    }
}
