package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ThemeTest {

    @Test
    public void getStylesheetPath_allThemes_stylesheetExists() {
        for (Theme theme : Theme.values()) {
            assertNotNull(Theme.class.getResource(theme.getStylesheetPath()),
                    "Missing stylesheet for " + theme + ": " + theme.getStylesheetPath());
        }
    }

    @Test
    public void getDisplayName_allThemes_returnsLowerCaseName() {
        assertEquals("dark", Theme.DARK.getDisplayName());
        assertEquals("light", Theme.LIGHT.getDisplayName());
    }
}
