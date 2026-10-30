package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
        "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX =
        "The person index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW =
        "%1$d person(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
        "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_EMPTY_LIST =
        "Your current contact list is empty! Time to populate them with your"
        + " friend's details!";
    public static final String MESSAGE_UNKNOWN_PINNED_STATUS =
            "Please only specify 'true'/'false' for the contact's isPinned!";
    public static final String MESSAGE_DUPLICATE_PHONE =
            "This phone number is already used by another contact.";
    public static final String MESSAGE_DUPLICATE_EMAIL =
            "This email address is already used by another contact.";
    public static final String MESSAGE_DUPLICATE_PHONE_AND_EMAIL =
            "This phone number and email address are already used by existing contacts.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(
        Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields = Stream.of(duplicatePrefixes)
                                          .map(Prefix::toString)
                                          .collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns an error message stating whether the phone number, email address or both of {@code person}
     * are already used by any of {@code otherPersons}.
     * At least one of them must already be used.
     */
    public static String getDuplicateContactMessage(Person person, List<Person> otherPersons) {
        boolean isPhoneUsed = otherPersons.stream()
                .anyMatch(other -> other.getPhone().equals(person.getPhone()));
        boolean isEmailUsed = otherPersons.stream()
                .anyMatch(other -> other.getEmail().equals(person.getEmail()));
        assert isPhoneUsed || isEmailUsed : "Person must share a phone number or email address with another";

        if (isPhoneUsed && isEmailUsed) {
            return MESSAGE_DUPLICATE_PHONE_AND_EMAIL;
        }
        return isPhoneUsed ? MESSAGE_DUPLICATE_PHONE : MESSAGE_DUPLICATE_EMAIL;
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail());
        if (!person.getAddress().isEmpty()) {
            builder.append("; Address: ")
                    .append(person.getAddress());
        }
        builder.append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }
}
