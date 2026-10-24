package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.SortCriteria;
import seedu.address.model.person.SortOrder;
import seedu.address.testutil.PersonBuilder;

public class SortCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);

    private final SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_allCriteriaAndOrders_returnsWorkingCommand() throws Exception {
        Person alice = new PersonBuilder().withName("Alice").withEmail("z@example.com")
                .withPhone("200").build();
        Person bob = new PersonBuilder().withName("Bob").withEmail("a@example.com")
                .withPhone("300").build();
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
                model.addPerson(bob);
                model.addPerson(alice);
                String input = criteria.name().toLowerCase(Locale.ROOT) + " "
                        + order.name().toLowerCase(Locale.ROOT);

                String feedback = parser.parse(input).execute(model).getFeedbackToUser();

                boolean aliceFirst = (criteria != SortCriteria.EMAIL) == (order == SortOrder.ASC);
                assertEquals(aliceFirst ? List.of(alice, bob) : List.of(bob, alice),
                        model.getFilteredPersonList(), input);
                assertEquals(String.format(SortCommand.MESSAGE_SUCCESS,
                        criteria.getDisplayName(), order.getDisplayName()), feedback);
            }
        }
    }

    @Test
    public void parse_lowerAndUpperCaseArguments_returnsExpectedCommand() {
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                SortCommand expectedCommand = new SortCommand(criteria, order);
                String input = criteria.name() + " " + order.name();

                assertParseSuccess(parser, input.toLowerCase(Locale.ROOT), expectedCommand);
                assertParseSuccess(parser, input.toUpperCase(Locale.ROOT), expectedCommand);
            }
        }
    }

    @Test
    public void parse_mixedCaseArguments_returnsExpectedCommand() {
        assertParseSuccess(parser, "NaMe aSc", new SortCommand(SortCriteria.NAME, SortOrder.ASC));
        assertParseSuccess(parser, "nAmE DeSc", new SortCommand(SortCriteria.NAME, SortOrder.DESC));
        assertParseSuccess(parser, "EmAiL AsC", new SortCommand(SortCriteria.EMAIL, SortOrder.ASC));
        assertParseSuccess(parser, "eMaIl dEsC", new SortCommand(SortCriteria.EMAIL, SortOrder.DESC));
        assertParseSuccess(parser, "PhOnE aSc", new SortCommand(SortCriteria.PHONE, SortOrder.ASC));
        assertParseSuccess(parser, "pHoNe DeSc", new SortCommand(SortCriteria.PHONE, SortOrder.DESC));
    }

    @Test
    public void parse_mixedCaseAndWhitespace_acceptsArguments() throws Exception {
        Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
        String feedback = parser.parse(" \t NaMe \t DeSc \n ").execute(model).getFeedbackToUser();
        assertEquals(String.format(SortCommand.MESSAGE_SUCCESS,
                SortCriteria.NAME.getDisplayName(), SortOrder.DESC.getDisplayName()), feedback);
    }

    @Test
    public void parse_missingOrExtraArguments_throwsParseException() {
        for (String input : List.of("", "   ", "name", "asc", "name asc extra", "name asc email desc")) {
            assertParseFailure(parser, input, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_invalidCriteriaOrOrder_throwsParseException() {
        for (String input : List.of("address asc", "names desc", "name ascending", "email descending",
                "phone random", "asc name", "nameasc", "name, asc")) {
            assertParseFailure(parser, input, MESSAGE_INVALID_FORMAT);
        }
    }
}
