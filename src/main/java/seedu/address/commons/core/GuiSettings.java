package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

import java.awt.Point;
import java.io.Serializable;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * A Serializable class that contains the GUI settings.
 * Guarantees: immutable.
 */
public class GuiSettings implements Serializable {

    private static final double DEFAULT_HEIGHT = 600;
    private static final double DEFAULT_WIDTH = 740;
    private static final Theme DEFAULT_THEME = Theme.DARK;

    private final double windowWidth;
    private final double windowHeight;
    private final Point windowCoordinates;
    private final Theme theme;

    /**
     * Constructs a {@code GuiSettings} with the default height, width, position and theme.
     */
    public GuiSettings() {
        windowWidth = DEFAULT_WIDTH;
        windowHeight = DEFAULT_HEIGHT;
        windowCoordinates = null; // null represent no coordinates
        theme = DEFAULT_THEME;
    }

    /**
     * Constructs a {@code GuiSettings} with the specified height, width and position, and the default theme.
     */
    public GuiSettings(double windowWidth, double windowHeight, int xPosition, int yPosition) {
        this(windowWidth, windowHeight, xPosition, yPosition, DEFAULT_THEME);
    }

    /**
     * Constructs a {@code GuiSettings} with the specified height, width, position and theme.
     */
    public GuiSettings(double windowWidth, double windowHeight, int xPosition, int yPosition, Theme theme) {
        requireNonNull(theme);
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        windowCoordinates = new Point(xPosition, yPosition);
        this.theme = theme;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public Point getWindowCoordinates() {
        return windowCoordinates != null ? new Point(windowCoordinates) : null;
    }

    /**
     * Returns the saved theme, or the default theme if none was saved.
     */
    public Theme getTheme() {
        return theme != null ? theme : DEFAULT_THEME;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GuiSettings otherGuiSettings)) {
            return false;
        }

        return windowWidth == otherGuiSettings.windowWidth
                && windowHeight == otherGuiSettings.windowHeight
                && Objects.equals(windowCoordinates, otherGuiSettings.windowCoordinates)
                && getTheme() == otherGuiSettings.getTheme();
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowWidth, windowHeight, windowCoordinates, getTheme());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("windowWidth", windowWidth)
                .add("windowHeight", windowHeight)
                .add("windowCoordinates", windowCoordinates)
                .add("theme", getTheme())
                .toString();
    }
}
