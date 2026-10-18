package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

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

    public static final String MESSAGE_INVALID_INDEX = "An invalid list index was specified. "
            + "Please check again with list.";

    public static final String MESSAGE_ALREADY_PINNED = "The selected contact is already pinned! "
            + "Please try again with another contact.";

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
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }

        Person personToPin = lastShownList.get(index.getZeroBased());
        if (personToPin.isPinned()) {
            throw new CommandException(MESSAGE_ALREADY_PINNED);
        }

        Person pinnedPerson = personToPin.pinPerson();

        model.setPerson(personToPin, pinnedPerson);

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(pinnedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PinCommand otherPinCommand)) {
            return false;
        }

        return index.equals(otherPinCommand.index);
    }
}
