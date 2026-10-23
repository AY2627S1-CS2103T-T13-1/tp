package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparatorFactory;
import seedu.address.model.person.SortCriteria;
import seedu.address.model.person.SortOrder;

/**
 * Sorts persons within the address book
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String COMMAND_DESCRIPTION = "Sorts contacts according to specified criteria and order";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " name asc";

    public static final String MESSAGE_SUCCESS = "Contacts are now sorted by %s in %s order";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: SORT_CRITERIA (name|email|phone), SORT_ORDER (asc|desc)\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " SORT_CRITERIA SORT_ORDER";

    private final SortCriteria sortCriteria;
    private final SortOrder sortOrder;

    /**
     * Creates a SortCommand to sort the address book according to {@code sortCriteria} and {@code sortOrder}
     */
    public SortCommand(SortCriteria sortCriteria, SortOrder sortOrder) {
        requireAllNonNull(sortCriteria, sortOrder);
        this.sortCriteria = sortCriteria;
        this.sortOrder = sortOrder;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Comparator<Person> comparator = PersonComparatorFactory.createComparator(sortCriteria, sortOrder);
        model.sortAddressBook(comparator);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                sortCriteria.getDisplayName(), sortOrder.getDisplayName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SortCommand otherSortCommand)) {
            return false;
        }

        return sortCriteria.equals(otherSortCommand.sortCriteria) && sortOrder.equals(otherSortCommand.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortCriteria, sortOrder);
    }
}
