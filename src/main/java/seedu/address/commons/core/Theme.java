package seedu.address.commons.core;

import java.util.Locale;

/**
 * Represents the color themes available in the app.
 */
public enum Theme {
    DARK("/view/DarkTheme.css"),
    LIGHT("/view/LightTheme.css");

    private final String stylesheetPath;

    Theme(String stylesheetPath) {
        this.stylesheetPath = stylesheetPath;
    }

    public String getStylesheetPath() {
        return stylesheetPath;
    }

    /**
     * Returns the name of this theme as shown to the user, e.g. "dark".
     */
    public String getDisplayName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
