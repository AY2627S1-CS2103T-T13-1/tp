package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Reverses the most recent command that changed the contact list.
 */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String COMMAND_DESCRIPTION = "Reverses the most recent command that changed the contact list.";

    public static final String MESSAGE_SUCCESS = "Undid the most recent change to your contact list.";

    public static final String MESSAGE_NOTHING_TO_UNDO = "There are no changes to undo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canUndo()) {
            throw new CommandException(MESSAGE_NOTHING_TO_UNDO);
        }

        model.undo();
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
