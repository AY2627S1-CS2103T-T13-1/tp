package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class RedoCommandTest {

    private final Model model =
            new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
    private final Model expectedModel =
            new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);

    @Test
    public void execute_noUndoneChanges_throwsCommandException() {
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_changeWithoutUndo_throwsCommandException() {
        model.deletePerson(ALICE);
        model.commit();

        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_oneUndoneChange_restoresUndoneState() {
        model.deletePerson(ALICE);
        model.commit();
        model.undo();

        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_multipleUndoneChanges_restoresStatesInOrder() {
        model.deletePerson(ALICE);
        model.commit();
        model.deletePerson(BENSON);
        model.commit();
        model.undo();
        model.undo();

        expectedModel.deletePerson(ALICE);
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        expectedModel.deletePerson(BENSON);
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_newChangeAfterUndo_throwsCommandException() {
        model.deletePerson(ALICE);
        model.commit();
        model.undo();
        model.deletePerson(CARL);
        model.commit();

        assertCommandFailure(new RedoCommand(), model, RedoCommand.MESSAGE_NOTHING_TO_REDO);
    }

    @Test
    public void execute_filteredList_showsAllContacts() {
        model.deletePerson(ALICE);
        model.commit();
        model.undo();
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        expectedModel.deletePerson(ALICE);

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_pinnedContact_keepsPinnedContactsOnTop() {
        model.setPerson(CARL, CARL.pinPerson());
        model.commit();
        model.undo();

        expectedModel.setPerson(CARL, CARL.pinPerson());

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
