package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class ThemeTest {

    /** Stylesheets that use the color placeholders defined by each theme. */
    private static final String[] STYLESHEETS_USING_PLACEHOLDERS = {
        "/view/Base.css", "/view/Extensions.css", "/view/UserGuideAccessWindow.css"
    };

    /** Matches a placeholder used as a value, e.g. "theme-bg" in "color: theme-bg;", but not ".theme-toggle". */
    private static final Pattern USED_PLACEHOLDER = Pattern.compile("(?<![\\w.#-])(theme-[a-z-]+)");

    /** Matches a placeholder being defined, e.g. "theme-bg" in "theme-bg: #000000;". */
    private static final Pattern DEFINED_PLACEHOLDER = Pattern.compile("(theme-[a-z-]+)\\s*:");

    @Test
    public void getStylesheetPath_allThemes_stylesheetExists() {
        for (Theme theme : Theme.values()) {
            assertNotNull(Theme.class.getResource(theme.getStylesheetPath()),
                    "Missing stylesheet for " + theme + ": " + theme.getStylesheetPath());
        }
    }

    @Test
    public void getStylesheetPath_allThemes_defineAllUsedPlaceholders() throws IOException {
        Set<String> usedPlaceholders = new TreeSet<>();
        for (String path : STYLESHEETS_USING_PLACEHOLDERS) {
            usedPlaceholders.addAll(findAll(USED_PLACEHOLDER, readResource(path)));
        }
        assertFalse(usedPlaceholders.isEmpty());

        for (Theme theme : Theme.values()) {
            Set<String> missingPlaceholders = new TreeSet<>(usedPlaceholders);
            missingPlaceholders.removeAll(findAll(DEFINED_PLACEHOLDER, readResource(theme.getStylesheetPath())));
            assertTrue(missingPlaceholders.isEmpty(),
                    theme.getStylesheetPath() + " does not define " + missingPlaceholders);
        }
    }

    @Test
    public void getDisplayName_allThemes_returnsLowerCaseName() {
        assertEquals("dark", Theme.DARK.getDisplayName());
        assertEquals("light", Theme.LIGHT.getDisplayName());
    }

    @Test
    public void fromName_validName_returnsTheme() {
        assertEquals(Theme.DARK, Theme.fromName("DARK"));
        assertEquals(Theme.LIGHT, Theme.fromName("light"));
        assertEquals(Theme.LIGHT, Theme.fromName("LiGhT"));
    }

    @Test
    public void fromName_invalidName_returnsNull() {
        assertNull(Theme.fromName("blue"));
        assertNull(Theme.fromName(""));
        assertNull(Theme.fromName(null));
    }

    private static String readResource(String path) throws IOException {
        try (InputStream stream = Theme.class.getResourceAsStream(path)) {
            assertNotNull(stream, "Missing stylesheet: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Set<String> findAll(Pattern pattern, String text) {
        Set<String> matches = new TreeSet<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }
}
