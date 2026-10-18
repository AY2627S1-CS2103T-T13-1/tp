package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Pins a person within the address book.
 */
public class PinCommand extends Command {

    public static final String COMMAND_WORD = "pin";

    public static final String COMMAND_DESCRIPTION = "Pins a contact at the specified list index";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "The following contact has been pinned: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: LIST_INDEX (must be a positive integer)\n"
            + COMMAND_SAMPLE_USAGE;

    private final Index index;

    /**
     * Creates a PinCommand to pin the contact specified at {@code index}.
     *
     * @param index list index of contact to be pinned.
     */
    public PinCommand(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
