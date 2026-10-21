package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.model.alias.AliasRegistry.MESSAGE_REGEX_MISMATCH;

import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.alias.AliasRegistry;

/**
 * Parses input arguments and creates a new AliasCommand object.
 */
public class AliasCommandParser implements Parser<AliasCommand> {
    private static final int COMMAND_WORD_INDEX = 0;
    private static final int ALIAS_INDEX = 1;

    @Override
    public AliasCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String[] arguments = parseArguments(args);
        String alias = arguments[ALIAS_INDEX];

        if (!AliasRegistry.isValidAlias(alias)) {
            throw new ParseException(MESSAGE_REGEX_MISMATCH);
        }

        return new AliasCommand(arguments[COMMAND_WORD_INDEX], alias);
    }

    private String[] parseArguments(String args) throws ParseException {
        String[] arguments = args.trim().split("\\s+", 3);

        if (arguments.length != 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AliasCommand.MESSAGE_USAGE));
        }
        return arguments;
    }
}
