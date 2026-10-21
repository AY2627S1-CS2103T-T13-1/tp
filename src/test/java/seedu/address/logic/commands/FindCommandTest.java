package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.FindCommand.MESSAGE_CONTACTS_FOUND;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesCriteriaPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);

    @Test
    public void equals() {
        PersonMatchesCriteriaPredicate firstPredicate = createPredicate("first");
        PersonMatchesCriteriaPredicate secondPredicate = createPredicate("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(createPredicate("first"));
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_noMatches_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_FOUND, 0);
        Predicate<Person> predicate = person -> false;
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleCriteria_onePersonFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_FOUND, 1);
        PersonMatchesCriteriaPredicate predicate = new PersonMatchesCriteriaPredicate(
                Optional.of("EL"),
                Optional.of("948"),
                Optional.empty(),
                Optional.empty(),
                Set.of());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(ELLE::equals);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ELLE), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleMatches_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_CONTACTS_FOUND, 2);
        Predicate<Person> predicate = person -> BENSON.equals(person) || ELLE.equals(person);
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(BENSON, ELLE), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesCriteriaPredicate predicate = createPredicate("keyword");
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Creates a predicate that matches {@code name} as a case-insensitive substring.
     */
    private PersonMatchesCriteriaPredicate createPredicate(String name) {
        return new PersonMatchesCriteriaPredicate(Optional.of(name),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Set.of());
    }
}
