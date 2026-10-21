package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Deletes a person by their currently displayed list index or all persons whose complete tag sets match
 * the supplied tags by category and case-insensitive name. Tag-based deletion searches
 * the entire address book, including persons hidden by the current filter.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String COMMAND_DESCRIPTION = "Deletes a contact identified either by their"
            + " index number used in the displayed person list, or at least one tag supplied.\n"
            + " If all supplied tags match, the person is deleted.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " [LIST_INDEX] "
            + "OR [-t TAG-CATEGORY TAG_NAME]...";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " 1, "
            + COMMAND_WORD + " -t others Friend";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: [LIST_INDEX] (must be a positive integer) OR [-t TAG_CATEGORY TAG_NAME]...\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Successfully removed %1$s from your address book.";

    public static final String MESSAGE_INVALID_INDEX = "An invalid list index was specified. "
        + "Please check again with list.";

    private static final Logger logger = LogsCenter.getLogger(DeleteCommand.class);

    private final Index targetIndex;

    private final Set<Tag> tagList;

    /**
     * Constructs a command to delete the person at the given index in the displayed list.
     *
     * @param targetIndex Index of the person to delete.
     * @throws NullPointerException if {@code targetIndex} is null.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
        this.tagList = null;
    }

    /**
     * Constructs a command to delete all persons in the entire address book whose complete tag sets
     * match the supplied tags by category and case-insensitive name.
     *
     * @param tagList Nonempty set of tags to match against the entire address book.
     * @throws NullPointerException if {@code tagList} or any of its elements is null.
     * @throws IllegalArgumentException if {@code tagList} is empty.
     */
    public DeleteCommand(Set<Tag> tagList) {
        requireAllNonNull(tagList);
        checkArgument(!tagList.isEmpty(), "At least one tag must be supplied.");
        this.tagList = Set.copyOf(tagList);
        this.targetIndex = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert (targetIndex != null) ^ (tagList != null)
                : "DeleteCommand must have exactly one deletion mode";

        if (targetIndex != null) {
            Person personToDelete = deletePersonAtIndex(model);
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
        }

        List<Person> personsToDelete = deletePeopleWithTags(model);
        if (personsToDelete.isEmpty()) {
            return new CommandResult("No matching persons were found to be deleted.");
        }
        return new CommandResult(String.format("Successfully removed %d matching contact(s) from your address book.",
                personsToDelete.size()));
    }

    /**
     * Collects and deletes all persons in the entire address book with exactly the supplied tags,
     * matching by category and case-insensitive name.
     * Returns the deleted persons, or an empty list if no persons match.
     */
    private List<Person> deletePeopleWithTags(Model model) {
        int originalSize = model.getAddressBook().getPersonList().size();
        Set<String> normalizedTargetTags = normalizeTags(tagList);
        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                .filter(person -> normalizeTags(person.getTags()).equals(normalizedTargetTags))
                .toList();

        logger.fine(() -> "Bulk delete matched " + personsToDelete.size()
                + " of " + originalSize
                + " contacts using exact, case-insensitive tag-set matching");

        for (Person person : personsToDelete) {
            model.deletePerson(person);
        }

        assert model.getAddressBook().getPersonList().size() == originalSize - personsToDelete.size()
                : "Bulk deletion must remove exactly the matched number of contacts";

        logger.info(() -> "Bulk delete removed " + personsToDelete.size()
                + " contacts from the address book");

        return personsToDelete;
    }

    /**
     * Returns lowercase tag strings in {@code category:name} format for comparison without changing the supplied tags.
     */
    private Set<String> normalizeTags(Set<Tag> tags) {
        return tags.stream()
                .map(tag -> (tag.getTagCategory() + ":" + tag.tagName)
                        .toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    /**
     * Deletes and returns the person at the supplied index in the displayed list.
     *
     * @throws CommandException if the displayed list is empty or the index is out of bounds.
     */
    private Person deletePersonAtIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (lastShownList.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_EMPTY_LIST);
        }

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        int originalSize = model.getAddressBook().getPersonList().size();
        model.deletePerson(personToDelete);
        assert model.getAddressBook().getPersonList().size() == originalSize - 1
                : "Index deletion must remove exactly one contact";
        return personToDelete;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return Objects.equals(targetIndex, otherDeleteCommand.targetIndex)
                && Objects.equals(tagList, otherDeleteCommand.tagList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("tagList", tagList)
                .toString();
    }
}
