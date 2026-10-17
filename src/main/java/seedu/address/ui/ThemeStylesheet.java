package seedu.address.ui;

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
     */
    static void apply(Scene scene, Theme theme) {
        assert scene != null && theme != null;
        ObservableList<String> stylesheets = scene.getStylesheets();
        for (Theme t : Theme.values()) {
            stylesheets.remove(getUrl(t));
        }
        stylesheets.add(getUrl(theme));
    }

    private static String getUrl(Theme theme) {
        return ThemeStylesheet.class.getResource(theme.getStylesheetPath()).toExternalForm();
    }
}
