package seedu.address.ui;

import java.net.URL;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import seedu.address.commons.core.Theme;

/**
 * Swaps the color palette stylesheet of a {@code Scene} so that windows can share the same theme.
 */
final class ThemeStylesheet {

    private ThemeStylesheet() {}

    /**
     * Replaces any theme stylesheet in {@code scene} with the stylesheet of {@code theme}.
     * Returns false and leaves {@code scene} unchanged if the stylesheet of {@code theme} cannot be found.
     */
    static boolean apply(Scene scene, Theme theme) {
        assert scene != null && theme != null;
        URL newUrl = getUrl(theme);
        if (newUrl == null) {
            return false;
        }

        ObservableList<String> stylesheets = scene.getStylesheets();
        for (Theme existingTheme : Theme.values()) {
            URL url = getUrl(existingTheme);
            if (url != null) {
                stylesheets.remove(url.toExternalForm());
            }
        }
        stylesheets.add(newUrl.toExternalForm());
        return true;
    }

    /**
     * Returns the location of the stylesheet of {@code theme}, or null if it cannot be found.
     */
    private static URL getUrl(Theme theme) {
        return ThemeStylesheet.class.getResource(theme.getStylesheetPath());
    }
}
