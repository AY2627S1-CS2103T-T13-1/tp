package seedu.address.commons.core;

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
}
