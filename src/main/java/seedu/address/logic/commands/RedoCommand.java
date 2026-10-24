package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Reapplies the most recent change that was reversed by an undo command.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";

    public static final String COMMAND_DESCRIPTION = "Reapplies the most recent change that was undone.";

    public static final String MESSAGE_SUCCESS =
            "Redid the most recently undone change to your contact list.";

    public static final String MESSAGE_NOTHING_TO_REDO = "There are no undone changes to redo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (!model.canRedo()) {
            throw new CommandException(MESSAGE_NOTHING_TO_REDO);
        }

        model.redo();
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
