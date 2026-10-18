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

public class PinCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToPin = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        PinCommand pinCommand = new PinCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(PinCommand.MESSAGE_SUCCESS,
                Messages.format(personToPin));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToPin, personToPin.pinPerson());

        assertCommandSuccess(pinCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_outOfBoundsIndex_commandExceptionThrown() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        PinCommand pinCommand = new PinCommand(outOfBoundIndex);

        assertCommandFailure(pinCommand, model, PinCommand.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void execute_alreadyPinnedIndex_commandExceptionThrown() {
        Person unpinnedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person pinnedPerson = unpinnedPerson.pinPerson();
        model.setPerson(unpinnedPerson, pinnedPerson);

        PinCommand pinCommand = new PinCommand(INDEX_FIRST_PERSON);

        assertCommandFailure(pinCommand, model, PinCommand.MESSAGE_ALREADY_PINNED);
    }
}
