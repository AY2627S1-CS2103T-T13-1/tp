package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.Messages;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String OTHERS_TAG_TYPE = "others";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    private static final String PINNED = "true";
    private static final String UNPINNED = "false";

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_pinnedStatus_returnsPinnedPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, PINNED);

        assertEquals(BENSON.pinPerson(), person.toModelType());
    }

    @Test
    public void toModelType_unpinnedStatus_returnsUnpinnedPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, UNPINNED);

        assertEquals(BENSON.unpinPerson(), person.toModelType());
    }

    @Test
    public void toModelType_pinnedPerson_preservesPinnedStatus() throws Exception {
        Person pinnedPerson = BENSON.pinPerson();
        JsonAdaptedPerson person = new JsonAdaptedPerson(pinnedPerson);

        assertEquals(pinnedPerson, person.toModelType());
    }

    @Test
    public void toModelType_unpinnedPerson_preservesPinnedStatus() throws Exception {
        Person unpinnedPerson = BENSON.unpinPerson();
        JsonAdaptedPerson person = new JsonAdaptedPerson(unpinnedPerson);

        assertEquals(unpinnedPerson, person.toModelType());
    }

    @Test
    public void toModelType_nullPinnedStatus_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, null);

        assertThrows(IllegalValueException.class, Messages.MESSAGE_UNKNOWN_PINNED_STATUS, person::toModelType);
    }

    @Test
    public void toModelType_emptyPinnedStatus_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, "");

        assertThrows(IllegalValueException.class, Messages.MESSAGE_UNKNOWN_PINNED_STATUS, person::toModelType);
    }

    @Test
    public void toModelType_invalidPinnedStatus_throwsIllegalValueException() {
        String[] invalidStatuses = {
            "TRUE", "FALSE", "True", "False", " true", "false ", " ", "1", "0", "yes", "pinned"
        };
        for (String invalidStatus : invalidStatuses) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_ADDRESS, VALID_TAGS, invalidStatus);

            assertThrows(IllegalValueException.class, Messages.MESSAGE_UNKNOWN_PINNED_STATUS, person::toModelType);
        }
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, UNPINNED);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, UNPINNED);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nameAtLengthBoundaries_returnsPerson() throws Exception {
        for (int length : new int[] {1, Name.MAXIMUM_NAME_LENGTH - 1, Name.MAXIMUM_NAME_LENGTH}) {
            String name = "A".repeat(length);
            JsonAdaptedPerson person = new JsonAdaptedPerson(name, VALID_PHONE, VALID_EMAIL,
                    VALID_ADDRESS, VALID_TAGS, UNPINNED);

            assertEquals(new PersonBuilder(BENSON).withName(name).build(), person.toModelType());
        }
    }

    @Test
    public void toModelType_nameOutsideLengthBoundaries_throwsIllegalValueException() {
        JsonAdaptedPerson personWithEmptyName = new JsonAdaptedPerson("", VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, UNPINNED);
        assertThrows(IllegalValueException.class, Name.MESSAGE_CONSTRAINTS, personWithEmptyName::toModelType);
        for (int length : new int[] {Name.MAXIMUM_NAME_LENGTH + 1, Name.MAXIMUM_NAME_LENGTH + 2}) {
            String name = "A".repeat(length);
            JsonAdaptedPerson person = new JsonAdaptedPerson(name, VALID_PHONE, VALID_EMAIL,
                    VALID_ADDRESS, VALID_TAGS, UNPINNED);
            assertThrows(IllegalValueException.class, Name.MESSAGE_EXCEED_MAX_LENGTH, person::toModelType);
        }
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_TAGS, UNPINNED);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, UNPINNED);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_phoneAtLengthBoundaries_returnsPerson() throws Exception {
        for (int length : new int[] {3, 4, 14, 15}) {
            String phone = "1".repeat(length);
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, phone, VALID_EMAIL,
                    VALID_ADDRESS, VALID_TAGS, UNPINNED);

            assertEquals(new PersonBuilder(BENSON).withPhone(phone).build(), person.toModelType());
        }
    }

    @Test
    public void toModelType_phoneOutsideLengthBoundaries_throwsIllegalValueException() {
        String[] invalidPhones = {"11", "1".repeat(16), "1".repeat(17), "(+65)123456789012345"};
        for (String phone : invalidPhones) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, phone, VALID_EMAIL,
                    VALID_ADDRESS, VALID_TAGS, UNPINNED);

            assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_TAGS, UNPINNED);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null,
                VALID_ADDRESS, VALID_TAGS, UNPINNED);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_emailAtMaximumLengthBoundary_returnsPerson() throws Exception {
        String domain = "@example.com";
        for (int length : new int[] {Email.MAXIMUM_EMAIL_LENGTH - 1, Email.MAXIMUM_EMAIL_LENGTH}) {
            String email = "a".repeat(length - domain.length()) + domain;
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, email,
                    VALID_ADDRESS, VALID_TAGS, UNPINNED);

            assertEquals(new PersonBuilder(BENSON).withEmail(email).build(), person.toModelType());
        }
    }

    @Test
    public void toModelType_emailAboveMaximumLength_throwsIllegalValueException() {
        String domain = "@example.com";
        for (int length : new int[] {Email.MAXIMUM_EMAIL_LENGTH + 1, Email.MAXIMUM_EMAIL_LENGTH + 2}) {
            // Preserve a valid email format so only the length constraint is violated.
            String email = "a".repeat(length - domain.length()) + domain;
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, email,
                    VALID_ADDRESS, VALID_TAGS, UNPINNED);

            assertThrows(IllegalValueException.class, Email.MESSAGE_EXCEED_MAX_LENGTH, person::toModelType);
        }
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_TAGS, UNPINNED);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_addressTooLong_throwsIllegalValueException() {
        String longAddress = "a".repeat(Address.MAXIMUM_ADDRESS_LENGTH + 1);
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                longAddress, VALID_TAGS, UNPINNED);
        String expectedMessage = Address.MESSAGE_EXCEED_MAX_LENGTH;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_returnsPersonWithEmptyAddress() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                null, VALID_TAGS, UNPINNED);
        assertEquals(new PersonBuilder(BENSON).withoutAddress().build(), person.toModelType());
    }

    @Test
    public void toModelType_emptyAddress_returnsPersonWithEmptyAddress() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, "",
                VALID_TAGS, UNPINNED);
        assertEquals(new PersonBuilder(BENSON).withoutAddress().build(), person.toModelType());
    }

    @Test
    public void toModelType_personWithoutAddress_returnsSamePerson() throws Exception {
        Person personWithoutAddress = new PersonBuilder(BENSON).withoutAddress().build();
        JsonAdaptedPerson person = new JsonAdaptedPerson(personWithoutAddress);
        assertEquals(personWithoutAddress, person.toModelType());
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG, OTHERS_TAG_TYPE));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, invalidTags, UNPINNED);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_fiveDistinctTags_returnsPerson() throws Exception {
        Person expectedPerson = new PersonBuilder(BENSON).withTags(new OthersTag("one"), new OthersTag("two"),
                new OthersTag("three"), new OthersTag("four"), new OthersTag("five")).build();

        assertEquals(expectedPerson, new JsonAdaptedPerson(expectedPerson).toModelType());
    }

    @Test
    public void toModelType_sixDistinctTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> tags = List.of("one", "two", "three", "four", "five", "six").stream()
                .map(name -> new JsonAdaptedTag(name, OTHERS_TAG_TYPE))
                .collect(Collectors.toList());
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, tags, UNPINNED);

        assertThrows(IllegalValueException.class, Tag.MESSAGE_TOO_MANY_TAGS, person::toModelType);
    }

    @Test
    public void toModelType_sixTagEntriesWithFiveDistinctTags_returnsPerson() throws Exception {
        Person expectedPerson = new PersonBuilder(BENSON).withTags(new OthersTag("one"), new OthersTag("two"),
                new OthersTag("three"), new OthersTag("four"), new OthersTag("five")).build();
        List<JsonAdaptedTag> tags = expectedPerson.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList());
        tags.add(new JsonAdaptedTag("one", OTHERS_TAG_TYPE));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, tags, UNPINNED);

        assertEquals(expectedPerson, person.toModelType());
    }

}
