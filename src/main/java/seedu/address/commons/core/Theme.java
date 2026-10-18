package seedu.address.commons.core;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

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

    /**
     * Returns the theme with the given {@code name}, ignoring case, or null if there is no such theme.
     * Used by {@code ThemeCommandParser}, and when reading preferences.json so that an unknown theme does not
     * stop the other preferences from loading.
     */
    @JsonCreator
    public static Theme fromName(String name) {
        for (Theme theme : values()) {
            if (theme.name().equalsIgnoreCase(name)) {
                return theme;
            }
        }
        return null;
    }
}
