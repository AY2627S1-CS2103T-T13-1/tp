package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = String.format("Help Menu\n========\n"
            + AddCommand.COMMAND_SYNTAX + "\n\t"
            + AddCommand.COMMAND_DESCRIPTION + "\n\t"
            + AddCommand.COMMAND_SAMPLE_USAGE + "\n\n"
            + DeleteCommand.COMMAND_SYNTAX + "\n\t"
            + DeleteCommand.COMMAND_DESCRIPTION + "\n\t"
            + DeleteCommand.COMMAND_SAMPLE_USAGE + "\n\n"
            + ListCommand.COMMAND_WORD + "\n\t"
            + ListCommand.COMMAND_DESCRIPTION + "\n\n"
            + FindCommand.COMMAND_SYNTAX + "\n\t"
            + FindCommand.COMMAND_DESCRIPTION + "\n\t"
            + FindCommand.COMMAND_SAMPLE_USAGE + "\n\n"
            + EditCommand.COMMAND_SYNTAX + "\n\t"
            + EditCommand.COMMAND_DESCRIPTION + "\n\t"
            + EditCommand.COMMAND_SAMPLE_USAGE);

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false);
    }
}
