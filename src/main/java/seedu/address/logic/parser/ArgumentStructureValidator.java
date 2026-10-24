package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.stream.Stream;

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
     * @param args the original command arguments as entered by the user
     * @param arguments the tokenized arguments and their associated values
     * @param supportedPrefixes the prefixes supported by the command
     * @param commandName the name of the command being validated
     * @throws ParseException if an unknown parameter, unexpected preamble,
     *         empty value, or duplicate non-tag parameter is found
     */
    public static void validate(
            String args,
            ArgumentMultimap arguments,
            Prefix[] supportedPrefixes,
            String commandName) throws ParseException {

        boolean isTagValue = false;

        String[] tokens = args.trim().split("\\s+");
        for (String token : tokens) {
            if (Stream.of(supportedPrefixes)
                    .anyMatch(prefix -> prefix.getPrefix().equals(token))) {
                isTagValue = token.equals(PREFIX_TAG.getPrefix());
                continue;
            }

            if (token.startsWith("-")
                    && token.length() > 1
                    && !isTagValue) {
                throw new ParseException(
                        "Unknown " + commandName + " parameter: '" + token + "'.");
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
}
