package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FilenameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Filename(null));
    }

    @Test
    public void constructor_invalidFilename_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Filename(""));
        assertThrows(IllegalArgumentException.class, () -> new Filename("   "));
        assertThrows(IllegalArgumentException.class, () -> new Filename("sample@file"));
        assertThrows(IllegalArgumentException.class, () -> new Filename("sample.json"));
    }

    @Test
    public void isValidName() {
        assertThrows(NullPointerException.class, () -> Filename.isValidName(null));

        assertFalse(Filename.isValidName(""));
        assertFalse(Filename.isValidName("   "));
        assertFalse(Filename.isValidName("sample@file"));
        assertFalse(Filename.isValidName("sample.json"));
        assertFalse(Filename.isValidName(" sample"));

        assertTrue(Filename.isValidName("sample"));
        assertTrue(Filename.isValidName("sample file"));
        assertTrue(Filename.isValidName("12345"));
        assertTrue(Filename.isValidName("Sample File 2026"));
    }

    @Test
    public void isValidLength() {
        assertTrue(Filename.isValidLength("a".repeat(Filename.MAXIMUM_NAME_LENGTH)));
        assertFalse(Filename.isValidLength("a".repeat(Filename.MAXIMUM_NAME_LENGTH + 1)));
    }

    @Test
    public void toString_returnsFilename() {
        Filename filename = new Filename("sample file");

        assertTrue(filename.toString().equals("sample file"));
    }

    @Test
    public void equals() {
        Filename filename = new Filename("sample file");

        assertTrue(filename.equals(filename));
        assertFalse(filename.equals(null));
        assertFalse(filename.equals(5.0f));
        assertTrue(filename.equals(new Filename("sample file")));
        assertFalse(filename.equals(new Filename("other file")));
    }

    @Test
    public void hashCode_sameFilename_returnsSameHashCode() {
        Filename filename = new Filename("sample file");
        Filename sameFilename = new Filename("sample file");

        assertEquals(filename.hashCode(), sameFilename.hashCode());
    }
}
