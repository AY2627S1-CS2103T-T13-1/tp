package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Lists all registered aliases and their corresponding command words.
 */
public class ListAliasCommand extends Command {

    public static final String COMMAND_WORD = "list_alias";

    public static final String COMMAND_DESCRIPTION = "Displays all saved alias mappings.";

    public static final String MESSAGE_SUCCESS_PREFIX = "Registered alias(es): ";

    public static final String MESSAGE_FAILURE_EMPTY = "There are no aliases currently registered.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Map<String, String> aliases = model.getAliases();

        if (aliases.isEmpty()) {
            return new CommandResult(MESSAGE_FAILURE_EMPTY);
        }

        String mappings = aliases.entrySet().stream()
                // alias is the key and command word is the value
                .map(entry -> entry.getValue() + " : " + entry.getKey())
                .reduce((x, y) -> x + "\n" + y)
                .orElse("");

        return new CommandResult(MESSAGE_SUCCESS_PREFIX + "\n" + mappings);
    }
}
