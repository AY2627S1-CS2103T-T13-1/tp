package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_emptyRemark_success() {
        assertDoesNotThrow(() -> new Remark(""));
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Some Remark");

        assertTrue(remark.equals(new Remark("Some Remark")));
        assertTrue(remark.equals(remark));

        assertFalse(remark.equals(null));
        assertFalse(remark.equals(new Remark("Another Remark")));
    }
}
