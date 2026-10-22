package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.SortCriteria;
import seedu.address.model.person.SortOrder;

public class SortCommand extends Command {

    public static final String COMMAND_WORD = "SortCommand";

    public static final String COMMAND_DESCRIPTION = "Sorts contacts according to specified criteria and order";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " name asc";

    public static final String MESSAGE_SUCCESS = "Contacts are now sorted by %s in %s order";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: SORT_CRITERIA (name|email|phone), SORT_ORDER (asc|desc)\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " SORT_CRITERIA SORT_ORDER";

    private final SortCriteria sortCriteria;
    private final SortOrder sortOrder;

    public SortCommand(SortCriteria sortCriteria, SortOrder sortOrder) {
        this.sortCriteria = sortCriteria;
        this.sortOrder = sortOrder;
    }

    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        return new CommandResult("");
    }
}
