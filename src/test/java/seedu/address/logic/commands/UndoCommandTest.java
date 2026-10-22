package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class UndoCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
    private final Model expectedModel =
            new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);

    @Test
    public void execute_noChanges_throwsCommandException() {
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_onlyUnchangedCommits_throwsCommandException() {
        model.commit();
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_oneChange_restoresPreviousState() {
        model.deletePerson(ALICE);
        model.commit();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandFailure(new UndoCommand(), model, UndoCommand.MESSAGE_NOTHING_TO_UNDO);
    }

    @Test
    public void execute_multipleChanges_restoresStatesInReverseOrder() {
        model.deletePerson(ALICE);
        model.commit();
        model.deletePerson(BENSON);
        model.commit();

        Model expectedModelWithoutAlice =
                new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
        expectedModelWithoutAlice.deletePerson(ALICE);

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModelWithoutAlice);
        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_filteredList_showsAllContacts() {
        model.deletePerson(ALICE);
        model.commit();
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_pinnedContact_keepsPinnedContactsOnTop() {
        model.setPerson(BENSON, BENSON.pinPerson());
        model.commit();
        model.deletePerson(ALICE);
        model.commit();

        expectedModel.setPerson(BENSON, BENSON.pinPerson());
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
