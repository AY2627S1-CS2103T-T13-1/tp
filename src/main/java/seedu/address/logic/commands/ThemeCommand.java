package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.Theme;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;

/**
 * Changes the color theme of the application.
 */
public class ThemeCommand extends Command {

    public static final String COMMAND_WORD = "theme";
    public static final String COMMAND_DESCRIPTION = "Changes the color theme.";
    public static final String COMMAND_SYNTAX = COMMAND_WORD + " dark|light";
    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " light";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + COMMAND_DESCRIPTION + "\n"
            + "Parameters: dark|light\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_SUCCESS = "Theme changed to %1$s.";

    private final Theme theme;

    /**
     * Creates a ThemeCommand to change to the specified {@code Theme}.
     */
    public ThemeCommand(Theme theme) {
        requireNonNull(theme);
        this.theme = theme;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(String.format(MESSAGE_SUCCESS, theme.name().toLowerCase()), false, theme);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ThemeCommand otherThemeCommand)) {
            return false;
        }

        return theme == otherThemeCommand.theme;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("theme", theme)
                .toString();
    }
}
