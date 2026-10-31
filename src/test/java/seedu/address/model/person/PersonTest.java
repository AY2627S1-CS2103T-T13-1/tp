package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_OTHERS_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.OthersTag;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void constructor_fewerThanMaxTags_success() {
        for (int tagCount = 0; tagCount < Tag.MAX_TAGS_PER_PERSON; tagCount++) {
            Person person = new PersonBuilder().withTags(generateTags(tagCount)).build();
            assertEquals(tagCount, person.getTags().size());
        }
    }

    @Test
    public void constructor_exactlyMaxTags_success() {
        Person person = new PersonBuilder().withTags(generateTags(Tag.MAX_TAGS_PER_PERSON)).build();
        assertEquals(Tag.MAX_TAGS_PER_PERSON, person.getTags().size());
    }

    @Test
    public void constructor_moreThanMaxTags_throwsIllegalArgumentException() {
        for (int excessTags = 1; excessTags <= 2; excessTags++) {
            Tag[] tags = generateTags(Tag.MAX_TAGS_PER_PERSON + excessTags);
            assertThrows(IllegalArgumentException.class,
                    Tag.MESSAGE_TOO_MANY_TAGS, () -> new PersonBuilder().withTags(tags).build());
        }
    }

    private static Tag[] generateTags(int count) {
        return IntStream.range(0, count)
                .mapToObj(index -> new OthersTag("tag" + index))
                .toArray(Tag[]::new);
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(new OthersTag(VALID_TAG_OTHERS_HUSBAND)).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // same phone, all other attributes different -> returns true
        editedAlice = new PersonBuilder(BOB).withPhone(ALICE.getPhone().value).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same email, all other attributes different -> returns true
        editedAlice = new PersonBuilder(BOB).withEmail(ALICE.getEmail().value).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns true
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(new OthersTag(VALID_TAG_OTHERS_HUSBAND)).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void equals_tagNameDiffersOnlyInCase_returnsFalse() {
        Person original = new PersonBuilder(ALICE).withTags(new OthersTag("Friend")).build();
        Person edited = new PersonBuilder(original).withTags(new OthersTag("friend")).build();

        assertEquals(original.getTags(), edited.getTags());
        assertFalse(original.equals(edited));
        assertFalse(edited.equals(original));
        assertTrue(original.isSamePerson(edited));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
