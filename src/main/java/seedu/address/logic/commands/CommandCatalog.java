package seedu.address.logic.commands;

import java.util.Set;

public class CommandCatalog {
    public static final Set<String> BUILT_IN_COMMAND_WORDS = Set.of(
            AddCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD,
            DeleteCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD,
            FindCommand.COMMAND_WORD,
            ListCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD,
            PinCommand.COMMAND_WORD,
            UnpinCommand.COMMAND_WORD,
            ThemeCommand.COMMAND_WORD
    );
}
