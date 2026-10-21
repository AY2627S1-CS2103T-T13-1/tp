package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.FacultyTag;
import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);

    @Test
    public void constructor_emptyTags_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new DeleteCommand(Set.<Tag>of()));
    }

    @Test
    public void execute_originalTagSetCleared_deletesUsingOriginalTags() throws Exception {
        Person matchingPerson = new PersonBuilder(ALICE).withTags(new OthersTag("coder")).build();
        Model tagModel = modelWithPersons(matchingPerson, CARL);
        Set<Tag> tags = new HashSet<>(matchingPerson.getTags());
        DeleteCommand command = new DeleteCommand(tags);

        tags.clear();
        command.execute(tagModel);

        assertEquals(List.of(CARL), tagModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_tagsDifferentCase_deletesAllMatches() throws Exception {
        Person firstMatch = new PersonBuilder(ALICE)
                .withTags(new OthersTag("CODeR"), new FacultyTag("Science")).build();
        Person secondMatch = new PersonBuilder(BENSON)
                .withTags(new OthersTag("coder"), new FacultyTag("SCIENCE")).build();
        Model tagModel = modelWithPersons(firstMatch, secondMatch);

        new DeleteCommand(Set.of(new OthersTag("CoDeR"), new FacultyTag("science"))).execute(tagModel);

        assertTrue(tagModel.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_personHasExtraTag_personRetained() throws Exception {
        Person person = new PersonBuilder()
                .withTags(new OthersTag("coder"),
                        new FacultyTag("Science"),
                        new OthersTag("Friend")).build();
        Model tagModel = modelWithPersons(person);

        new DeleteCommand(Set.of(new OthersTag("coder"), new FacultyTag("Science"))).execute(tagModel);

        assertEquals(List.of(person), tagModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_personHasDifferentTagCategory_personRetained() throws Exception {
        Person person = new PersonBuilder()
                .withTags(new FacultyTag("coder"), new FacultyTag("Science")).build();
        Model tagModel = modelWithPersons(person);

        new DeleteCommand(Set.of(new OthersTag("coder"), new FacultyTag("Science"))).execute(tagModel);

        assertEquals(List.of(person), tagModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_personMissingTag_personRetained() throws Exception {
        Person person = new PersonBuilder().withTags(new OthersTag("Coder")).build();
        Model tagModel = modelWithPersons(person);

        new DeleteCommand(
                Set.of(new OthersTag("coder"), new FacultyTag("Science"))).execute(tagModel);

        assertEquals(List.of(person), tagModel.getAddressBook().getPersonList());
    }

    @Test
    public void execute_tagsEmptyAddressBook_returnsNoMatchesMessage() {
        Model emptyModel = modelWithPersons();
        DeleteCommand command = new DeleteCommand(Set.of(new OthersTag("coder")));

        assertCommandSuccess(command, emptyModel, "No matching persons were found to be deleted.",
                modelWithPersons());
    }

    @Test
    public void execute_tagsNoMatches_returnsNoMatchesMessage() {
        Model tagModel = modelWithPersons(CARL);
        DeleteCommand command = new DeleteCommand(Set.of(new OthersTag("coder")));

        assertCommandSuccess(command, tagModel, "No matching persons were found to be deleted.",
                modelWithPersons(CARL));
    }

    @Test
    public void execute_tagsMultipleMatches_reportsDeletedCount() {
        Person firstMatch = new PersonBuilder(ALICE).withTags(new OthersTag("coder")).build();
        Person secondMatch = new PersonBuilder(BENSON).withTags(new OthersTag("coder")).build();
        Model tagModel = modelWithPersons(firstMatch, secondMatch, CARL);
        DeleteCommand command = new DeleteCommand(Set.of(new OthersTag("coder")));

        assertCommandSuccess(command, tagModel,
                "Successfully removed 2 matching contact(s) from your address book.",
                modelWithPersons(CARL));
    }

    @Test
    public void execute_tagsFilteredList_deletesVisibleAndHiddenMatches() throws Exception {
        Person visibleMatch = new PersonBuilder(ALICE).withTags(new OthersTag("coder")).build();
        Person hiddenMatch = new PersonBuilder(BENSON).withTags(new OthersTag("coder")).build();
        Model tagModel = modelWithPersons(visibleMatch, hiddenMatch, CARL);
        tagModel.updateFilteredPersonList(visibleMatch::equals);

        new DeleteCommand(Set.of(new OthersTag("coder"))).execute(tagModel);

        assertEquals(List.of(CARL), tagModel.getAddressBook().getPersonList());
        assertTrue(tagModel.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_tagsEmptyFilteredList_deletesHiddenMatches() throws Exception {
        Person matchingPerson = new PersonBuilder(ALICE).withTags(new OthersTag("coder")).build();
        Model tagModel = modelWithPersons(matchingPerson, CARL);
        showNoPerson(tagModel);

        new DeleteCommand(Set.of(new OthersTag("coder"))).execute(tagModel);

        assertEquals(List.of(CARL), tagModel.getAddressBook().getPersonList());
        assertTrue(tagModel.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_indexEmptyAddressBook_throwsCommandException() {
        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), modelWithPersons(), Messages.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_indexEmptyFilteredList_throwsCommandException() {
        showNoPerson(model);
        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), model, Messages.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, DeleteCommand.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(), BUILT_IN_COMMAND_WORDS);
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, DeleteCommand.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void equals_sameTagSets_returnsTrue() {
        DeleteCommand first = new DeleteCommand(Set.of(new OthersTag("coder"),
                new FacultyTag("Science")));
        DeleteCommand second = new DeleteCommand(Set.of(new FacultyTag("Science"),
                new OthersTag("coder")));

        assertEquals(first, second);
    }

    @Test
    public void equals_differentTagSets_returnsFalse() {
        DeleteCommand first = new DeleteCommand(Set.of(new OthersTag("coder")));
        DeleteCommand second = new DeleteCommand(Set.of(new OthersTag("friend")));

        assertFalse(first.equals(second));
    }

    @Test
    public void equals_tagsAndIndex_returnsFalse() {
        DeleteCommand tagsCommand = new DeleteCommand(Set.of(new OthersTag("coder")));
        DeleteCommand indexCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        assertFalse(tagsCommand.equals(indexCommand));
        assertFalse(indexCommand.equals(tagsCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + ", tagList=null}";
        assertEquals(expected, deleteCommand.toString());
    }

    @Test
    public void toStringMethod_tags() {
        Set<Tag> tags = Set.of(new OthersTag("coder"));
        DeleteCommand deleteCommand = new DeleteCommand(tags);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=null, tagList=" + tags + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Returns a model containing only the supplied persons.
     */
    private Model modelWithPersons(Person... persons) {
        Model tagModel = new ModelManager(BUILT_IN_COMMAND_WORDS);
        for (Person person : persons) {
            tagModel.addPerson(person);
        }
        return tagModel;
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
