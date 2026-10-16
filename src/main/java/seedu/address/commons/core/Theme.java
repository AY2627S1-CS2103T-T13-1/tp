package seedu.address.commons.core;

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
}
