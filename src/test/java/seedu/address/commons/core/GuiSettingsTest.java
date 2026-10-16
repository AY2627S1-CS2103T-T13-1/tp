package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class GuiSettingsTest {
    @Test
    public void toStringMethod() {
        GuiSettings guiSettings = new GuiSettings();
        String expected = GuiSettings.class.getCanonicalName() + "{windowWidth=" + guiSettings.getWindowWidth()
                + ", windowHeight=" + guiSettings.getWindowHeight() + ", windowCoordinates="
                + guiSettings.getWindowCoordinates() + ", theme=" + guiSettings.getTheme() + "}";
        assertEquals(expected, guiSettings.toString());
    }

    @Test
    public void constructor_noTheme_defaultsToDark() {
        assertEquals(Theme.DARK, new GuiSettings().getTheme());
        assertEquals(Theme.DARK, new GuiSettings(1, 2, 3, 4).getTheme());
    }

    @Test
    public void equals_differentTheme_returnsFalse() {
        GuiSettings dark = new GuiSettings(1, 2, 3, 4, Theme.DARK);
        GuiSettings light = new GuiSettings(1, 2, 3, 4, Theme.LIGHT);
        assertNotEquals(dark, light);
    }
}
