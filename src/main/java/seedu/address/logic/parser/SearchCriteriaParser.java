package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.Set;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;


/**
 * Parses and validates contact search criteria from tokenized command
 * arguments.
 *
 * This parser extracts the optional name, phone number, email address,
 * address, and tag values from an {@link ArgumentMultimap}. It also delegates
 * field validation to {@link ParserUtil} before creating a
 * {@link SearchCriteria} object.
 *
 */
public class SearchCriteriaParser {
    /**
     * Prevents instantiation of this utility class.
     */
    private SearchCriteriaParser() {}

    /**
     * Parses contact search criteria from tokenized command arguments.
     *
     * @param arguments the tokenized command arguments containing the search
     *                  criteria
     * @return the parsed and validated search criteria
     * @throws ParseException if any supplied name, phone number, email address,
     *         address, or tag is invalid
     */
    public static SearchCriteria parse(ArgumentMultimap arguments)
            throws ParseException {

        Optional<String> name = arguments.getValue(PREFIX_NAME)
                .map(value -> value.trim().replaceAll("\\s+", " "));

        Optional<String> phone = arguments.getValue(PREFIX_PHONE);
        Optional<String> email = arguments.getValue(PREFIX_EMAIL);
        Optional<String> address = arguments.getValue(PREFIX_ADDRESS);

        Set<Tag> tags = ParserUtil.parseTags(
                arguments.getAllValues(PREFIX_TAG));

        if (name.isPresent()) {
            ParserUtil.parseName(name.get());
        }
        if (phone.isPresent()) {
            ParserUtil.parsePhone(phone.get());
        }
        if (email.isPresent()) {
            ParserUtil.parseEmail(email.get());
        }
        if (address.isPresent()) {
            ParserUtil.parseAddress(address.get());
        }

        return new SearchCriteria(name, phone, email, address, tags);
    }
}
