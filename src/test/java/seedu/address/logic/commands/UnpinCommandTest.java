package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class UnpinCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToPin = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personToUnpin = personToPin.pinPerson();
        model.setPerson(personToPin, personToUnpin);

        UnpinCommand unpinCommand = new UnpinCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(UnpinCommand.MESSAGE_SUCCESS,
                Messages.format(personToUnpin));

        ModelManager expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        assertCommandSuccess(unpinCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_outOfBoundsIndex_commandExceptionThrown() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UnpinCommand unpinCommand = new UnpinCommand(outOfBoundIndex);

        assertCommandFailure(unpinCommand, model, PinCommand.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void execute_alreadyUnpinnedIndex_commandExceptionThrown() {
        UnpinCommand unpinCommand = new UnpinCommand(INDEX_FIRST_PERSON);
        assertCommandFailure(unpinCommand, model, UnpinCommand.MESSAGE_ALREADY_UNPINNED);
    }
}