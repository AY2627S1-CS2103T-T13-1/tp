package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class ImportRequestTest {

    @Test
    public void constructor_storesFilePath() {
        Path filePath = Path.of("data", "exports", "contacts.json");

        ImportRequest importRequest = new ImportRequest(filePath);

        assertSame(filePath, importRequest.getFilePath());
    }

    @Test
    public void constructor_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ImportRequest(null));
    }

    @Test
    public void equals_sameFilePath_returnsTrue() {
        Path filePath = Path.of("contacts.json");

        ImportRequest firstRequest = new ImportRequest(filePath);
        ImportRequest secondRequest = new ImportRequest(filePath);

        assertEquals(firstRequest, secondRequest);
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        ImportRequest importRequest = new ImportRequest(Path.of("contacts.json"));

        assertEquals(importRequest, importRequest);
    }

    @Test
    public void equals_differentFilePath_returnsFalse() {
        ImportRequest firstRequest = new ImportRequest(Path.of("contacts.json"));
        ImportRequest secondRequest = new ImportRequest(Path.of("other-contacts.json"));

        assertNotEquals(firstRequest, secondRequest);
    }

    @Test
    public void equals_nullOrDifferentType_returnsFalse() {
        ImportRequest importRequest = new ImportRequest(Path.of("contacts.json"));

        assertNotEquals(importRequest, null);
        assertNotEquals(importRequest, Path.of("contacts.json"));
    }

    @Test
    public void hashCode_sameFilePath_returnsSameHashCode() {
        Path filePath = Path.of("contacts.json");

        ImportRequest firstRequest = new ImportRequest(filePath);
        ImportRequest secondRequest = new ImportRequest(filePath);

        assertEquals(firstRequest.hashCode(), secondRequest.hashCode());
    }
}
