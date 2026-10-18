package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.Theme;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** The application should exit. */
    private final boolean exit;

    /** The theme to switch to, or null if the theme should not change. */
    private final Theme theme;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean exit, Theme theme) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.exit = exit;
        this.theme = theme;
    }

    /**
     * Constructs a {@code CommandResult} with the specified fields and no theme change.
     */
    public CommandResult(String feedbackToUser, boolean exit) {
        this(feedbackToUser, exit, null);
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isExit() {
        return exit;
    }

    public Optional<Theme> getTheme() {
        return Optional.ofNullable(theme);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && exit == otherCommandResult.exit
                && theme == otherCommandResult.theme;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, exit, theme);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("exit", exit)
                .add("theme", theme)
                .toString();
    }

}
