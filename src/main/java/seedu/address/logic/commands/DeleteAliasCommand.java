package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Deletes a registered alias.
 */
public class DeleteAliasCommand extends Command {

    public static final String COMMAND_WORD = "delete_alias";

    public static final String COMMAND_DESCRIPTION = "Deletes and deregisters a user-specified alias.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " ALIAS_TO_DELETE";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " f";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + COMMAND_DESCRIPTION + "\n"
            + "Parameters: ALIAS\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_SUCCESS = "Alias '%1$s' was deleted.";

    private final String alias;

    /**
     * Constructs a command to remove {@code alias}.
     *
     * @param alias the alias to remove.
     */
    public DeleteAliasCommand(String alias) {
        requireNonNull(alias);
        this.alias = alias;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            model.deleteAlias(alias);
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, alias));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteAliasCommand otherDelAliasCommand)) {
            return false;
        }

        return alias.equals(otherDelAliasCommand.alias);
    }

    @Override
    public int hashCode() {
        return alias.hashCode();
    }
}
