package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Lists all registered aliases and their corresponding command words.
 */
public class ListAliasCommand extends Command {

    public static final String COMMAND_WORD = "list_alias";

    public static final String COMMAND_DESCRIPTION = "Displays all saved alias mappings.";

    public static final String MESSAGE_SUCCESS_PREFIX = "Registered alias(es): ";

    public static final String MESSAGE_NO_ALIASES = "There are no aliases currently registered.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Map<String, String> aliases = model.getAliases();

        if (aliases.isEmpty()) {
            return new CommandResult(MESSAGE_NO_ALIASES);
        }

        Map<String, String> groupedAliases = aliases.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(
                                Map.Entry::getKey,
                                Collectors.joining(", "))));

        String mappings = groupedAliases.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));

        return new CommandResult(MESSAGE_SUCCESS_PREFIX + "\n" + mappings);
    }
}
