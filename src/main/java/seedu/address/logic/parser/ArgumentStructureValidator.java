package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Validates the common structural rules for command arguments.
 * This validator checks that arguments do not contain unknown parameters,
 * unexpected text before the first prefix, empty parameter values, or repeated
 * parameters. The tag prefix is allowed to occur multiple times. Hyphen-leading
 * words within tag values are validated as tag content rather than parameters.
 */
public class ArgumentStructureValidator {
    /**
     * Prevents instantiation of this utility class.
     */
    private ArgumentStructureValidator() {}

    /**
     * Validates the structure of the supplied command arguments.
     *
     * @param arguments the tokenized arguments and their associated values
     * @param supportedPrefixes the prefixes supported by the command
     * @param commandName the name of the command being validated
     * @throws ParseException if an unknown parameter, unexpected preamble,
     *         empty value, or duplicate non-tag parameter is found
     */
    public static void validate(
            ArgumentMultimap arguments,
            Prefix[] supportedPrefixes,
            String commandName) throws ParseException {

        validateUnknownParameters(arguments.getPreamble(), commandName);
        for (Prefix prefix : supportedPrefixes) {
            if (prefix.equals(PREFIX_TAG)) {
                continue;
            }

            for (String value : arguments.getAllValues(prefix)) {
                validateUnknownParameters(value, commandName);
            }
        }

        if (!arguments.getPreamble().isEmpty()) {
            throw new ParseException(
                    "Unexpected text before the first parameter: '"
                            + arguments.getPreamble() + "'.");
        }

        for (Prefix prefix : supportedPrefixes) {
            if (arguments.getAllValues(prefix)
                    .stream()
                    .anyMatch(String::isEmpty)) {
                throw new ParseException(
                        "The value for '" + prefix + "' cannot be empty.");
            }

            if (arguments.getAllValues(prefix).size() > 1
                    && !prefix.equals(PREFIX_TAG)) {
                throw new ParseException(
                        "The " + prefix
                                + " parameter may only be specified once.");
            }
        }
    }

    /**
     * Rejects parameter-like tokens in text outside tokenized tag values.
     * Treats text enclosed in double quotes as a single token.
     * Supported parameter prefixes have already been removed by the tokenizer.
     *
     * @throws ParseException if the text contains an unknown parameter
     */
    private static void validateUnknownParameters(String text, String commandName) throws ParseException {
        String trimmedText = StringUtil.stripWhitespace(text);
        if (trimmedText.isEmpty()) {
            return;
        }

        boolean insideQuotes = false;
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < trimmedText.length(); i++) {
            char currentCharacter = trimmedText.charAt(i);

            if (currentCharacter == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }

            boolean isWhitespace = Character.isWhitespace(currentCharacter)
                    || Character.isSpaceChar(currentCharacter);

            if (!insideQuotes && isWhitespace) {
                validateParameterToken(token.toString(), commandName);
                token.setLength(0);
            } else {
                token.append(currentCharacter);
            }
        }

        if (insideQuotes) {
            throw new ParseException(ImportCommandParser.MESSAGE_FILENAME_CONTAINS_QUOTES);
        }
        validateParameterToken(token.toString(), commandName);
    }

    private static void validateParameterToken(String token, String commandName)
            throws ParseException {

        if (token.startsWith("-") && token.length() > 1) {
            throw new ParseException(
                    "Unknown " + commandName + " parameter: '" + token + "'.");
        }
    }
}
