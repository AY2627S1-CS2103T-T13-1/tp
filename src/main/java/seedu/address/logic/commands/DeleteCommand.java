package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using its displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String COMMAND_DESCRIPTION = "Deletes a contact identified by their"
            + " index number used in the displayed person list.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " LIST_INDEX";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: LIST_INDEX (must be a positive integer)\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Successfully removed %1$s from your address book.";

    public static final String MESSAGE_INVALID_INDEX = "An invalid list index was specified. "
        + "Please check again with list.";

    private final Index targetIndex;

    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
