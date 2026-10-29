package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.DeleteAliasCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.alias.AliasRegistry;

/**
 * Parses the input argument and creates a new DeleteAliasCommand object.
 */
public class DeleteAliasCommandParser implements Parser<DeleteAliasCommand> {
    @Override
    public DeleteAliasCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String alias = parseAlias(args);
        return new DeleteAliasCommand(alias);
    }

    private String parseAlias(String args) throws ParseException {
        String[] arguments = StringUtil.stripWhitespace(args).split("(?U)\\s+", 2);
        String alias = arguments[0];

        if (alias.isEmpty() || arguments.length != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteAliasCommand.MESSAGE_USAGE));
        }

        if (!AliasRegistry.isValidAlias(alias)) {
            throw new ParseException(AliasRegistry.MESSAGE_REGEX_MISMATCH);
        }
        return alias;
    }
}
