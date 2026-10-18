package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.JsonUtil;

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

    @Test
    public void equals_differentWindowCoordinates_returnsFalse() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4, Theme.DARK);
        assertNotEquals(guiSettings, new GuiSettings(1, 2, 30, 4, Theme.DARK));
    }

    @Test
    public void equals_sameTheme_returnsTrue() {
        GuiSettings light = new GuiSettings(1, 2, 3, 4, Theme.LIGHT);
        assertEquals(light, new GuiSettings(1, 2, 3, 4, Theme.LIGHT));
        assertEquals(light.hashCode(), new GuiSettings(1, 2, 3, 4, Theme.LIGHT).hashCode());
    }

    @Test
    public void getTheme_nullThemeInJson_returnsDark() throws Exception {
        GuiSettings guiSettings = JsonUtil.fromJsonString("{\"theme\" : null}", GuiSettings.class);
        assertEquals(Theme.DARK, guiSettings.getTheme());
    }

    @Test
    public void getTheme_lightThemeInJson_returnsLight() throws Exception {
        GuiSettings guiSettings = JsonUtil.fromJsonString("{\"theme\" : \"LIGHT\"}", GuiSettings.class);
        assertEquals(Theme.LIGHT, guiSettings.getTheme());
    }

    @Test
    public void getTheme_lowerCaseThemeInJson_returnsTheme() throws Exception {
        GuiSettings guiSettings = JsonUtil.fromJsonString("{\"theme\" : \"light\"}", GuiSettings.class);
        assertEquals(Theme.LIGHT, guiSettings.getTheme());
    }

    @Test
    public void getTheme_unknownThemeInJson_returnsDarkAndKeepsOtherSettings() throws Exception {
        GuiSettings guiSettings = JsonUtil.fromJsonString(
                "{\"windowWidth\" : 900.0, \"theme\" : \"BLUE\"}", GuiSettings.class);
        assertEquals(Theme.DARK, guiSettings.getTheme());
        assertEquals(900.0, guiSettings.getWindowWidth());
    }
}
