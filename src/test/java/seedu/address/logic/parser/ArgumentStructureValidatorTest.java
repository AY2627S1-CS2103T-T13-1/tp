package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentStructureValidatorTest {

    private static final Prefix[] SUPPORTED_PREFIXES = {
        PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG
    };

    @Test
    public void validate_validArguments_doesNotThrow() {
        String args = " -n Alice -t role student -t skill coding";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        assertDoesNotThrow(() -> validate(args, arguments));
    }

    @Test
    public void validate_unknownParameter_throwsParseException() {
        String args = " -n Alice -x value";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals("Unknown find parameter: '-x'.", exception.getMessage());
    }

    @Test
    public void validate_singleHyphenToken_doesNotThrow() {
        String args = " -n -";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        assertDoesNotThrow(() -> validate(args, arguments));
    }

    @Test
    public void validate_hyphenLeadingWordsInTagValue_doesNotThrow() {
        String args = " -t others -archived -t others coder -x value";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        assertDoesNotThrow(() -> validate(args, arguments));
    }

    @Test
    public void validate_unknownParameterAfterTagAndAnotherPrefix_throwsParseException() {
        String args = " -t others -archived -n Alice -x value";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals("Unknown find parameter: '-x'.", exception.getMessage());
    }

    @Test
    public void validate_unknownParameterBeforeTag_throwsParseException() {
        String args = " -x value -t others -archived";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals("Unknown find parameter: '-x'.", exception.getMessage());
    }

    @Test
    public void validate_knownParameterAfterTag_throwsParseException() {
        String args = " -t others -p";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals("The value for '-p' cannot be empty.", exception.getMessage());
    }

    @Test
    public void validate_unexpectedPreamble_throwsParseException() {
        String args = "Alice -n Alice";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals(
                "Unexpected text before the first parameter: 'Alice'.",
                exception.getMessage());
    }

    @Test
    public void validate_emptyValue_throwsParseException() {
        String args = " -n";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals("The value for '-n' cannot be empty.", exception.getMessage());
    }

    @Test
    public void validate_duplicateNonTagParameter_throwsParseException() {
        String args = " -n Alice -n Bob";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        ParseException exception = assertThrows(ParseException.class, ()
                -> validate(args, arguments));

        assertEquals("The -n parameter may only be specified once.", exception.getMessage());
    }

    @Test
    public void validate_duplicateTagParameter_doesNotThrow() {
        String args = " -t role student -t skill coding";
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, SUPPORTED_PREFIXES);

        assertDoesNotThrow(() -> validate(args, arguments));
    }

    private void validate(String args, ArgumentMultimap arguments) throws ParseException {
        ArgumentStructureValidator.validate(
                args, arguments, SUPPORTED_PREFIXES, "find");
    }
}
