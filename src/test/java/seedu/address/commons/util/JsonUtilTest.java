package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @TempDir
    public Path testFolder;

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    @Test
    public void jsonUtil_readJsonStringToObjectInstance_correctObject() throws IOException {
        SerializableTestClass actual = JsonUtil.fromJsonString(
                SerializableTestClass.JSON_STRING_REPRESENTATION, SerializableTestClass.class);

        assertEquals(SerializableTestClass.getNameTestValue(), actual.getName());
        assertEquals(SerializableTestClass.getListTestValues(), actual.getListOfLocalDateTimes());
        assertEquals(SerializableTestClass.getHashMapTestValues(), actual.getMapOfIntegerToString());
    }

    @Test
    public void jsonUtil_writeThenReadObjectToJson_correctObject() throws IOException {
        SerializableTestClass original = new SerializableTestClass();
        original.setTestValues();

        String json = JsonUtil.toJsonString(original);
        assertEquals(SerializableTestClass.JSON_STRING_REPRESENTATION, json);

        SerializableTestClass actual = JsonUtil.fromJsonString(json, SerializableTestClass.class);

        assertEquals(original.getName(), actual.getName());
        assertEquals(original.getListOfLocalDateTimes(), actual.getListOfLocalDateTimes());
        assertEquals(original.getMapOfIntegerToString(), actual.getMapOfIntegerToString());
    }

    @Test
    public void readJsonFileStrict_missingFile_returnsEmpty() throws DataLoadingException {
        assertFalse(JsonUtil.readJsonFileStrict(testFolder.resolve("missing.json"),
                String.class).isPresent());
    }

    @Test
    public void readJsonFileStrict_validString_returnsCorrectValue() throws IOException, DataLoadingException {
        Path filePath = testFolder.resolve("valid.json");
        FileUtil.writeToFile(filePath, "\"value\"");

        assertEquals(Optional.of("value"), JsonUtil.readJsonFileStrict(filePath, String.class));
    }

    @Test
    public void readJsonFileStrict_directory_throwsDataLoadingException() {
        assertThrows(DataLoadingException.class, () ->
                JsonUtil.readJsonFileStrict(testFolder, SerializableTestClass.class));
    }

    @Test
    public void readJsonFileStrict_duplicateKeys_throwsDataLoadingException() throws IOException {
        Path filePath = testFolder.resolve("duplicateKeys.json");
        FileUtil.writeToFile(filePath, "{\"find\":\"f\",\"find\":\"fd\"}");

        assertThrows(DataLoadingException.class, () ->
                JsonUtil.readJsonFileStrict(filePath, SerializableTestClass.class));
    }

    @Test
    public void readJsonFileStrict_nullRoot_throwsDataLoadingException() throws IOException {
        Path filePath = testFolder.resolve("nullRoot.json");
        FileUtil.writeToFile(filePath, "null");

        assertThrows(DataLoadingException.class, () ->
                JsonUtil.readJsonFileStrict(filePath, SerializableTestClass.class));
    }
}
