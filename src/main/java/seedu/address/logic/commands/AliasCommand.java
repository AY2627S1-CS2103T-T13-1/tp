package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.alias.AliasRegistry;

/**
 * Registers an alias for a built-in command.
 */
public class AliasCommand extends Command {

    public static final String COMMAND_WORD = "alias";

    public static final String COMMAND_DESCRIPTION = "Registers an alias for a command.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " COMMAND_WORD ALIAS_TO_SET";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " find f";

    public static final String MESSAGE_SUCCESS = "Alias '%1$s' was registered for command '%2$s'.";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": "
            + COMMAND_DESCRIPTION + "\n"
            + "Parameters: COMMAND_WORD ALIAS_TO_SET\n"
            + COMMAND_SAMPLE_USAGE;

    private final String commandWord;
    private final String alias;

    /**
     * Constructs an AliasCommand to register {@code commandWord} as {@code alias}.
     * @param commandWord the built-in command word.
     * @param alias the term to register.
     */
    public AliasCommand(String commandWord, String alias) {
        requireAllNonNull(commandWord, alias);
        this.commandWord = commandWord;
        this.alias = alias;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        try {
            AliasRegistry aliasRegistry = model.getAliasRegistry();
            aliasRegistry.register(commandWord, alias);
        } catch (IllegalArgumentException exception) {
            throw new CommandException(exception.getMessage());
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, alias, commandWord));
    }
}
