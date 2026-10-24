package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandCatalog.BUILT_IN_COMMAND_WORDS;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.SortCriteria;
import seedu.address.model.person.SortOrder;
import seedu.address.testutil.PersonBuilder;

public class SortCommandTest {
    private final Person alice = new PersonBuilder().withName("Alice").withEmail("z@example.com")
            .withPhone("200").build();
    private final Person bob = new PersonBuilder().withName("Bob").withEmail("a@example.com")
            .withPhone("300").build();
    private final Person charlie = new PersonBuilder().withName("Charlie").withEmail("m@example.com")
            .withPhone("100").build();

    @Test
    public void execute_allCriteriaAndOrders_sortsAndReportsSuccess() throws Exception {
        for (SortCriteria criteria : SortCriteria.values()) {
            for (SortOrder order : SortOrder.values()) {
                Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
                model.addPerson(charlie);
                model.addPerson(alice);
                model.addPerson(bob);
                List<Person> ascending = switch (criteria) {
                    case NAME -> List.of(alice, bob, charlie);
                    case EMAIL -> List.of(bob, charlie, alice);
                    case PHONE -> List.of(charlie, alice, bob);
                };
                List<Person> expected = order == SortOrder.ASC ? ascending
                        : List.of(ascending.get(2), ascending.get(1), ascending.get(0));

                CommandResult result = new SortCommand(criteria, order).execute(model);

                assertEquals(expected, model.getFilteredPersonList(), criteria + " " + order);
                assertEquals(String.format(SortCommand.MESSAGE_SUCCESS,
                        criteria.getDisplayName(), order.getDisplayName()), result.getFeedbackToUser());
            }
        }
    }

    @Test
    public void execute_filteredList_preservesFilterAndSortsBackingList() throws Exception {
        Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
        model.addPerson(charlie);
        model.addPerson(bob);
        model.addPerson(alice);
        model.updateFilteredPersonList(person -> !person.equals(bob));

        new SortCommand(SortCriteria.NAME, SortOrder.ASC).execute(model);

        assertEquals(List.of(alice, charlie), model.getFilteredPersonList());
        assertEquals(List.of(alice, bob, charlie), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_descendingSort_keepsPinnedPersonsFirst() throws Exception {
        Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
        model.addPerson(alice.pinPerson());
        model.addPerson(bob);
        model.addPerson(charlie);

        new SortCommand(SortCriteria.NAME, SortOrder.DESC).execute(model);

        assertEquals(List.of(alice.pinPerson(), charlie, bob), model.getFilteredPersonList());
    }

    @Test
    public void execute_thenAddAndEdit_preservesSelectedOrder() throws Exception {
        Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
        model.addPerson(alice);
        model.addPerson(charlie);
        new SortCommand(SortCriteria.NAME, SortOrder.DESC).execute(model);

        model.addPerson(bob);
        assertEquals(List.of(charlie, bob, alice), model.getFilteredPersonList());

        Person renamedAlice = new PersonBuilder(alice).withName("Zoe").build();
        model.setPerson(alice, renamedAlice);
        assertEquals(List.of(renamedAlice, charlie, bob), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyAndSingletonLists_success() throws Exception {
        Model model = new ModelManager(BUILT_IN_COMMAND_WORDS);
        new SortCommand(SortCriteria.NAME, SortOrder.ASC).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        model.addPerson(alice);
        new SortCommand(SortCriteria.NAME, SortOrder.DESC).execute(model);
        assertEquals(List.of(alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new SortCommand(SortCriteria.NAME, SortOrder.ASC).execute(null));
    }
}
