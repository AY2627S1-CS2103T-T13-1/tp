package seedu.address.logic.commands;

import seedu.address.model.Model;
import seedu.address.ui.UserGuideAccessWindow;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Help Menu\n========\n"
            + String.join("\n\n",
            formatHelpEntry(
                    AddCommand.COMMAND_SYNTAX,
                    AddCommand.COMMAND_DESCRIPTION,
                    AddCommand.COMMAND_SAMPLE_USAGE),
            formatHelpEntry(
                    DeleteCommand.COMMAND_SYNTAX,
                    DeleteCommand.COMMAND_DESCRIPTION,
                    DeleteCommand.COMMAND_SAMPLE_USAGE),
            formatHelpEntry(
                    ListCommand.COMMAND_WORD,
                    ListCommand.COMMAND_DESCRIPTION),
            formatHelpEntry(
                    FindCommand.COMMAND_SYNTAX,
                    FindCommand.COMMAND_DESCRIPTION,
                    FindCommand.COMMAND_SAMPLE_USAGE),
            formatHelpEntry(
                    EditCommand.COMMAND_SYNTAX,
                    EditCommand.COMMAND_DESCRIPTION,
                    EditCommand.COMMAND_SAMPLE_USAGE),
            formatHelpEntry(
                    "User Guide Link: " + UserGuideAccessWindow.USERGUIDE_URL));

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_HELP_MESSAGE);
    }

    // help command formatter
    private static String formatHelpEntry(String... lines) {
        return String.join("\n\t", lines);
    }
}
