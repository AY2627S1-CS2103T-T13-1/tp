package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.SortCriteria;
import seedu.address.model.person.SortOrder;
import seedu.address.testutil.PersonBuilder;

public class SortCommandHistoryTest {
    private final Person alice = new PersonBuilder().withName("Alice").withEmail("z@example.com")
            .withPhone("200").build();
    private final Person bob = new PersonBuilder().withName("Bob").withEmail("a@example.com")
            .withPhone("300").build();
    private final Person charlie = new PersonBuilder().withName("Charlie").withEmail("m@example.com")
            .withPhone("100").build();
    private final Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);

    @Test
    public void execute_addAddSortUndo_restoresOriginalOrderAndDefaultComparator() throws Exception {
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Model currentModel = new ModelManager(BUILT_IN_COMMAND_WORDS);
                executeAndCommit(currentModel, new AddCommand(charlie), new AddCommand(alice),
                        new SortCommand(criteria, order), new UndoCommand());
                assertOrder(currentModel, charlie, alice);
                assertTrue(currentModel.canRedo());

                // The default comparator preserves insertion order among unpinned contacts.
                // Adding Bob distinguishes it from every explicit criteria/order combination.
                executeAndCommit(currentModel, new AddCommand(bob));
                assertOrder(currentModel, charlie, alice, bob);
                assertFalse(currentModel.canRedo());
            }
        }
    }

    @Test
    public void execute_sortUndoRedo_restoresOrderAndActiveComparatorForAllCriteriaAndOrders() throws Exception {
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Model currentModel = new ModelManager(BUILT_IN_COMMAND_WORDS);
                executeAndCommit(currentModel, new AddCommand(charlie), new AddCommand(alice),
                        new SortCommand(criteria, order));
                List<Person> sortedState = List.copyOf(currentModel.getAddressBook().getPersonList());

                executeAndCommit(currentModel, new UndoCommand());
                assertOrder(currentModel, charlie, alice);
                executeAndCommit(currentModel, new RedoCommand());
                assertEquals(sortedState, currentModel.getAddressBook().getPersonList());
                assertEquals(sortedState, currentModel.getFilteredPersonList());
                assertFalse(currentModel.canRedo());

                // A restored snapshot alone is insufficient: future additions must use the restored comparator.
                executeAndCommit(currentModel, new AddCommand(bob));
                List<Person> ascending = switch (criteria) {
                    case NAME -> List.of(alice, bob, charlie);
                    case EMAIL -> List.of(bob, charlie, alice);
                    case PHONE -> List.of(charlie, alice, bob);
                };
                List<Person> expected = order == SortOrder.ASC ? ascending
                        : List.of(ascending.get(2), ascending.get(1), ascending.get(0));
                assertEquals(expected, currentModel.getAddressBook().getPersonList(), criteria + " " + order);
                assertEquals(expected, currentModel.getFilteredPersonList(), criteria + " " + order);
            }
        }
    }

    @Test
    public void execute_sortAddUndoRedoThenEdit_keepsSelectedComparator() throws Exception {
        executeAndCommit(model, new AddCommand(charlie), new AddCommand(alice),
                new SortCommand(SortCriteria.EMAIL, SortOrder.ASC), new AddCommand(bob));
        assertOrder(model, bob, charlie, alice);

        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, bob, charlie, alice);

        Person editedBob = new PersonBuilder(bob).withEmail("zz@example.com").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setEmail(editedBob.getEmail());
        executeAndCommit(model, new EditCommand(INDEX_FIRST_PERSON, descriptor));
        assertOrder(model, charlie, alice, editedBob);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, bob, charlie, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, charlie, alice, editedBob);
    }

    @Test
    public void execute_sortEditUndoSortUndoRedo_restoresPreviousAndNewComparators() throws Exception {
        executeAndCommit(model, new AddCommand(alice), new AddCommand(bob), new AddCommand(charlie),
                new SortCommand(SortCriteria.NAME, SortOrder.DESC));
        Person renamedBob = new PersonBuilder(bob).withName("Zoe").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.setName(renamedBob.getName());
        executeAndCommit(model, new EditCommand(INDEX_SECOND_PERSON, descriptor));
        assertOrder(model, renamedBob, charlie, alice);

        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, bob, alice);
        executeAndCommit(model, new SortCommand(SortCriteria.EMAIL, SortOrder.ASC));
        assertOrder(model, bob, charlie, alice);
        assertFalse(model.canRedo());
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, bob, alice);
        executeAndCommit(model, new RedoCommand(), new EditCommand(INDEX_FIRST_PERSON, descriptor));
        assertOrder(model, renamedBob, charlie, alice);
    }

    @Test
    public void execute_sortDeleteUndoSortUndoRedo_restoresDeletedContactAndSortHistory() throws Exception {
        executeAndCommit(model, new AddCommand(alice), new AddCommand(bob), new AddCommand(charlie),
                new SortCommand(SortCriteria.PHONE, SortOrder.DESC), new DeleteCommand(INDEX_FIRST_PERSON));
        assertOrder(model, alice, charlie);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, bob, alice, charlie);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, alice, charlie);

        executeAndCommit(model, new SortCommand(SortCriteria.NAME, SortOrder.DESC));
        assertOrder(model, charlie, alice);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, alice, charlie);
        executeAndCommit(model, new RedoCommand(), new AddCommand(bob));
        assertOrder(model, charlie, bob, alice);
    }

    @Test
    public void execute_multipleSortsUndoRedo_restoresEachSortAndSupportsNewBranch() throws Exception {
        executeAndCommit(model, new AddCommand(charlie), new AddCommand(alice), new AddCommand(bob),
                new SortCommand(SortCriteria.NAME, SortOrder.DESC),
                new SortCommand(SortCriteria.EMAIL, SortOrder.ASC),
                new SortCommand(SortCriteria.PHONE, SortOrder.DESC));
        assertOrder(model, bob, alice, charlie);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, bob, charlie, alice);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, bob, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, bob, charlie, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, bob, alice, charlie);

        executeAndCommit(model, new UndoCommand(), new UndoCommand(), new DeleteCommand(INDEX_FIRST_PERSON));
        assertOrder(model, bob, alice);
        assertFalse(model.canRedo());
        executeAndCommit(model, new AddCommand(charlie));
        assertOrder(model, charlie, bob, alice);
    }

    @Test
    public void execute_sortPinUnpinUndoRedo_keepsPinnedFirstAndRestoresComparator() throws Exception {
        executeAndCommit(model, new AddCommand(alice), new AddCommand(bob), new AddCommand(charlie),
                new SortCommand(SortCriteria.NAME, SortOrder.DESC), new PinCommand(INDEX_SECOND_PERSON));
        assertOrder(model, bob.pinPerson(), charlie, alice);
        executeAndCommit(model, new SortCommand(SortCriteria.EMAIL, SortOrder.DESC));
        assertOrder(model, bob.pinPerson(), alice, charlie);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, bob.pinPerson(), charlie, alice);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, bob, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, bob.pinPerson(), charlie, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, bob.pinPerson(), alice, charlie);

        executeAndCommit(model, new UnpinCommand(INDEX_FIRST_PERSON));
        assertOrder(model, alice, charlie, bob);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, bob.pinPerson(), alice, charlie);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, alice, charlie, bob);
    }

    @Test
    public void execute_sortClearUndoRedoAdd_keepsComparatorAcrossEmptyState() throws Exception {
        executeAndCommit(model, new AddCommand(charlie), new AddCommand(alice),
                new SortCommand(SortCriteria.NAME, SortOrder.DESC), new ClearCommand());
        assertOrder(model);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, alice);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, charlie, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model);

        executeAndCommit(model, new AddCommand(alice), new AddCommand(bob));
        assertOrder(model, bob, alice);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, alice);
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, bob, alice);
    }

    @Test
    public void execute_sortEmptyOrSingletonUndoRedo_recordsComparatorOnlyChange() throws Exception {
        for (boolean hasInitialContact : List.of(false, true)) {
            Model currentModel = new ModelManager(BUILT_IN_COMMAND_WORDS);
            if (hasInitialContact) {
                executeAndCommit(currentModel, new AddCommand(alice));
            }
            executeAndCommit(currentModel, new SortCommand(SortCriteria.NAME, SortOrder.DESC));
            assertTrue(currentModel.canUndo());
            executeAndCommit(currentModel, new UndoCommand());
            assertEquals(hasInitialContact ? List.of(alice) : List.of(), currentModel.getFilteredPersonList());
            assertTrue(currentModel.canRedo());
            executeAndCommit(currentModel, new RedoCommand());
            if (!hasInitialContact) {
                executeAndCommit(currentModel, new AddCommand(alice));
            }
            executeAndCommit(currentModel, new AddCommand(bob));
            assertOrder(currentModel, bob, alice);
        }
    }

    @Test
    public void execute_findSortUndoListRedo_sortsBackingListAndPreservesRedoHistory() throws Exception {
        executeAndCommit(model, new AddCommand(charlie), new AddCommand(alice), new AddCommand(bob),
                new FindCommand(person -> !person.equals(bob)),
                new SortCommand(SortCriteria.EMAIL, SortOrder.DESC));
        assertEquals(List.of(alice, charlie), model.getFilteredPersonList());
        assertEquals(List.of(alice, charlie, bob), model.getAddressBook().getPersonList());

        executeAndCommit(model, new UndoCommand());
        assertOrder(model, charlie, alice, bob);
        executeAndCommit(model, new ListCommand());
        assertTrue(model.canRedo());
        executeAndCommit(model, new RedoCommand());
        assertOrder(model, alice, charlie, bob);
        executeAndCommit(model, new DeleteCommand(INDEX_FIRST_PERSON));
        assertOrder(model, charlie, bob);
        executeAndCommit(model, new UndoCommand());
        assertOrder(model, alice, charlie, bob);
    }

    /**
     * Executes commands with the same commit policy as LogicManager: undo skips committing; redo commits.
     */
    private void executeAndCommit(Model targetModel, Command... commands) throws CommandException {
        for (Command command : commands) {
            command.execute(targetModel);
            if (!(command instanceof UndoCommand)) {
                targetModel.commit();
            }
        }
    }

    private void assertOrder(Model targetModel, Person... expectedPersons) {
        List<Person> expected = List.of(expectedPersons);
        assertEquals(expected, targetModel.getAddressBook().getPersonList());
        assertEquals(expected, targetModel.getFilteredPersonList());
    }
}
