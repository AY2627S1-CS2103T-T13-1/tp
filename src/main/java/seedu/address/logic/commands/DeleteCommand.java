package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Deletes a person by their displayed index or all persons whose complete tag sets match
 * the supplied tags, ignoring case. Tag-based deletion searches the entire address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String COMMAND_DESCRIPTION = "Deletes a contact identified either by their"
            + " index number used in the displayed person list, or at least one tag supplied."
            + " If all supplied tags match, the person is deleted.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " LIST_INDEX";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " 1, "
            + COMMAND_WORD + " -t others Friend";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: [LIST_INDEX] (must be a positive integer) OR [-t TAG_CATEGORY TAG_NAME]...\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Successfully removed %1$s from your address book.";

    public static final String MESSAGE_INVALID_INDEX = "An invalid list index was specified. "
        + "Please check again with list.";

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
     * Constructs a command to delete all persons whose tag sets match the supplied tags.
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
     * Collects and deletes all persons with exactly the supplied tags, ignoring case.
     * Returns the deleted persons, or an empty list if no persons match.
     */
    private List<Person> deletePeopleWithTags(Model model) {
        assert tagList != null;
        Set<String> normalizedTargetTags = normalizeTags(tagList);
        List<Person> personsToDelete = model.getAddressBook().getPersonList().stream()
                .filter(person -> normalizeTags(person.getTags()).equals(normalizedTargetTags))
                .toList();

        for (Person person : personsToDelete) {
            model.deletePerson(person);
        }
        return personsToDelete;
    }

    /**
     * Returns lowercase category:name keys for comparison without changing the supplied tags.
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

        assert targetIndex != null;
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
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
