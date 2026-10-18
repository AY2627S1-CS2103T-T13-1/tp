package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class PinCommand extends Command {

    public static final String MESSAGE_SUCCESS = "The following contact has been pinned: %1$s";

    private final Index pinIndex;

    public PinCommand(Index index) {
        this.pinIndex = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
