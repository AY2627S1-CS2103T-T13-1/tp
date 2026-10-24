package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.HashSet;
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
 * Deletes a person by their currently displayed list index or removes supplied tag instances.
 * Tag-based deletion deletes persons whose nonempty tag sets are fully covered by the supplied tags,
 * and removes matching tags from other persons. Matching uses category and case-insensitive name
 * across the entire address book, including persons hidden by the current filter.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String COMMAND_DESCRIPTION = "Deletes a contact either by its displayed index,"
            + " or removes all instances of the supplied tags (case-insensitive).\n"
            + "Contacts whose tags are all included in the supplied tags are removed.\n"
            + "Tag-based deletion includes contacts hidden by the current filter.";

    public static final String COMMAND_SYNTAX = COMMAND_WORD + " [LIST_INDEX] "
            + "OR [-t TAG_CATEGORY TAG_NAME]...";

    public static final String COMMAND_SAMPLE_USAGE = "Example: " + COMMAND_WORD + " 1, "
            + COMMAND_WORD + " -t others Friend";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": " + COMMAND_DESCRIPTION + "\n"
            + "Parameters: [LIST_INDEX] (must be a positive integer) OR [-t TAG_CATEGORY TAG_NAME]...\n"
            + COMMAND_SAMPLE_USAGE;

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Successfully removed %1$s from your address book.";

    public static final String MESSAGE_DELETE_TAGS_SUCCESS =
            "Successfully removed %d matching contact(s) from your address book.";

    public static final String MESSAGE_REMOVE_TAG_INSTANCES_SUCCESS = "All instances of tag(s) (%s) have been removed.";

    public static final String MESSAGE_NO_MATCHING_PERSONS = "No matching persons found!";

    public static final String MESSAGE_INVALID_INDEX = "An invalid list index was specified. "
        + "Please check again with list.";

    private static final Logger logger = LogsCenter.getLogger(DeleteCommand.class);

    private final Index targetIndex;

    private final Set<Tag> targetTags;

    /**
     * Constructs a command to delete the person at the given index in the displayed list.
     *
     * @param targetIndex Index of the person to delete.
     * @throws NullPointerException if {@code targetIndex} is null.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
        this.targetTags = null;
    }

    /**
     * Constructs a command to remove matching tag instances throughout the entire address book,
     * deleting persons whose nonempty tag sets are fully covered by the supplied tags.
     *
     * @param targetTags Nonempty set of tags to match against the entire address book.
     * @throws NullPointerException if {@code targetTags} or any of its elements is null.
     * @throws IllegalArgumentException if {@code targetTags} is empty.
     */
    public DeleteCommand(Set<Tag> targetTags) {
        requireAllNonNull(targetTags);
        checkArgument(!targetTags.isEmpty(), "At least one tag must be supplied.");
        this.targetTags = Set.copyOf(targetTags);
        this.targetIndex = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        assert (targetIndex != null) ^ (targetTags != null)
                : "DeleteCommand must have exactly one deletion mode";

        if (targetIndex != null) {
            Person personToDelete = deletePersonAtIndex(model);
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
        }

        return deletePeopleWithTags(model);
    }

    /**
     * Removes matching tags from a snapshot of the entire address book, matching by category and
     * case-insensitive name. Deletes persons whose nonempty tag sets are fully covered by the supplied
     * tags and leaves untagged persons unchanged. Reports the deleted count and matched tag names.
     */
    private CommandResult deletePeopleWithTags(Model model) throws CommandException {
        int originalSize = model.getAddressBook().getPersonList().size();
        Set<String> normalizedTargetTags = normalizeTags(targetTags);
        List<Person> snapshotOfPersonlist = new ArrayList<>(model.getAddressBook().getPersonList());
        List<Person> personsToDelete = new ArrayList<>();
        Set<String> matchedTags = new HashSet<>();

        if (snapshotOfPersonlist.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_EMPTY_LIST);
        }

        for (Person person : snapshotOfPersonlist) {
            if (person.getTags().isEmpty()) {
                continue;
            }

            Set<String> personTags = normalizeTags(person.getTags());
            personTags.stream()
                    .filter(normalizedTargetTags::contains)
                    .forEach(matchedTags::add);

            if (normalizedTargetTags.containsAll(personTags)) {
                model.deletePerson(person);
                personsToDelete.add(person);
                continue;
            }

            Set<Tag> remainingTags = new HashSet<>(person.getTags());
            boolean changed = remainingTags.removeIf(tag -> normalizedTargetTags.contains(normalizeTag(tag)));
            if (changed) {
                Person editedPerson = new Person(
                        person.getName(),
                        person.getPhone(),
                        person.getEmail(),
                        person.getAddress(),
                        remainingTags,
                        person.isPinned());
                model.setPerson(person, editedPerson);
            }
        }

        assert model.getAddressBook().getPersonList().size() == originalSize - personsToDelete.size()
                : "Bulk deletion must remove exactly the matched number of contacts";

        logger.info(() -> "Bulk delete removed " + personsToDelete.size()
                + " contacts from the address book");

        if (matchedTags.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MATCHING_PERSONS);
        }

        String matchedTagNames = targetTags.stream()
                .filter(tag -> matchedTags.contains(normalizeTag(tag)))
                .map(tag -> tag.tagName)
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
        String tagInstanceRemovalMessage = String.format(MESSAGE_REMOVE_TAG_INSTANCES_SUCCESS, matchedTagNames);

        if (personsToDelete.isEmpty()) {
            return new CommandResult(tagInstanceRemovalMessage);
        }
        return new CommandResult(String.format(MESSAGE_DELETE_TAGS_SUCCESS, personsToDelete.size())
                + "\n" + tagInstanceRemovalMessage);
    }

    /**
     * Returns lowercase tag strings in {@code category:name} format for comparison without changing the supplied tags.
     */
    private Set<String> normalizeTags(Set<Tag> tags) {
        return tags.stream()
                .map(this::normalizeTag)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a lowercase category-and-name string for matching a tag.
     */
    private String normalizeTag(Tag tag) {
        return (tag.getTagCategory() + ":" + tag.tagName).toLowerCase(Locale.ROOT);
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
                && Objects.equals(targetTags, otherDeleteCommand.targetTags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("targetTags", targetTags)
                .toString();
    }
}
