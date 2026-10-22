package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.OthersTag;

public class SearchCriteriaParserTest {

    private static final Prefix[] SEARCH_PREFIXES = {
        PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG
    };

    @Test
    public void parse_allCriteria_returnsParsedCriteria() throws ParseException {
        String args = " -n Alice   Smith"
                + " -p 123456"
                + " -e alice@example.com"
                + " -a 123 Main Street #0505"
                + " -t others friend"
                + " -t others coder";

        SearchCriteria criteria = SearchCriteriaParser.parse(tokenize(args));

        assertEquals(Optional.of("Alice Smith"), criteria.getName());
        assertEquals(Optional.of("123456"), criteria.getPhone());
        assertEquals(Optional.of("alice@example.com"), criteria.getEmail());
        assertEquals(Optional.of("123 Main Street #0505"), criteria.getAddress());
        assertEquals(Set.of(new OthersTag("friend"), new OthersTag("coder")), criteria.getTags());
    }

    @Test
    public void parse_noCriteria_returnsEmptyCriteria() throws ParseException {
        SearchCriteria criteria = SearchCriteriaParser.parse(tokenize(""));

        assertEquals(Optional.empty(), criteria.getName());
        assertEquals(Optional.empty(), criteria.getPhone());
        assertEquals(Optional.empty(), criteria.getEmail());
        assertEquals(Optional.empty(), criteria.getAddress());
        assertTrue(criteria.getTags().isEmpty());
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        String args = " -n R@chel";

        assertThrows(ParseException.class, ()
                -> SearchCriteriaParser.parse(tokenize(args)));
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        String args = " -p +651234";

        assertThrows(ParseException.class, ()
                -> SearchCriteriaParser.parse(tokenize(args)));
    }

    @Test
    public void parse_partialEmail_throwsParseException() throws ParseException {
        String args = " -e example.com";

        SearchCriteria criteria = SearchCriteriaParser.parse(tokenize(args));

        assertEquals(Optional.of("example.com"), criteria.getEmail());
    }

    @Test
    public void parse_invalidAddress_throwsParseException() {
        String args = " -a ";

        assertThrows(ParseException.class, ()
                -> SearchCriteriaParser.parse(tokenize(args)));
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        String args = " -t #friend";

        assertThrows(ParseException.class, ()
                -> SearchCriteriaParser.parse(tokenize(args)));
    }

    private ArgumentMultimap tokenize(String args) {
        return ArgumentTokenizer.tokenize(args, SEARCH_PREFIXES);
    }
}
