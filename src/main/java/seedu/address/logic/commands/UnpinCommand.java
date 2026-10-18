package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Unpins a person within the address book.
 */
public class UnpinCommand extends Command {

    public static final String COMMAND_WORD = "unpin";

    public static final String COMMAND_DESCRIPTION = "Unpins a contact at the specified list index";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_SUCCESS = "The following contact has been unpinned: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: LIST_INDEX (must be a positive integer)\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_INVALID_INDEX = "An invalid list index was specified. "
            + "Please check again with list.";

    public static final String MESSAGE_ALREADY_UNPINNED = "The selected contact is already unpinned! "
            + "Please try again with another contact.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " LIST_INDEX";

    private final Index index;

    /**
     * Creates a UnpinCommand to unpin the contact specified at {@code index}.
     *
     * @param index list index of contact to be unpinned.
     */
    public UnpinCommand(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }

        Person personToUnpin = lastShownList.get(index.getZeroBased());
        if (!personToUnpin.isPinned()) {
            throw new CommandException(MESSAGE_ALREADY_UNPINNED);
        }
        Person unpinnedPerson = personToUnpin.unpinPerson();

        model.setPerson(personToUnpin, unpinnedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(unpinnedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnpinCommand otherUnpinCommand)) {
            return false;
        }

        return index.equals(otherUnpinCommand.index);
    }
}
