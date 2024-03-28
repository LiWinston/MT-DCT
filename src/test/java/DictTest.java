import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.Dict;

import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class DictTest {

    private static final String TEST_FILE_PATH = "test_dictionary.txt";
    private Dict dict;

    @Before
    public void setUp() {
        // Create a test dictionary file
        createTestDictionaryFile();
        // Initialize the dictionary with the test file
        dict = new Dict(TEST_FILE_PATH);
    }

    @After
    public void tearDown() {
        // Close the dictionary to save content to file
        dict.close();
        // Delete the test dictionary file
        deleteTestDictionaryFile();
    }

    @Test
    public void testSearch() {
        String word = "test";
        assertNull(dict.search(word)); // Word doesn't exist in the dictionary
        dict.add(word, "meaning1;meaning2");
        assertEquals("meaning1;meaning2", dict.search(word)); // Word exists, test search functionality
    }

    @Test
    public void testAdd() {
        String word = "test";
        assertTrue(dict.add(word, "meaning1;meaning2")); // Add new word to dictionary
        assertFalse(dict.add(word, "meaning3")); // Word already exists, should return false
    }

    @Test
    public void testDelete() {
        String word = "test";
        assertFalse(dict.delete(word)); // Word doesn't exist, should return false
        dict.add(word, "meaning1");
        assertTrue(dict.delete(word)); // Word exists, should return true after deletion
        assertNull(dict.search(word)); // Word should no longer exist in the dictionary
    }

    @Test
    public void testUpdate() {
        String word = "test";
        assertFalse(dict.update(word, "meaning1")); // Word doesn't exist, should return false
        dict.add(word, "meaning1");
        assertTrue(dict.update(word, "newmeaning")); // Update existing word, should return true
        assertEquals("newmeaning", dict.search(word)); // Verify word has been updated
    }

    // Helper method to create a test dictionary file
    private void createTestDictionaryFile() {
        try {
            File file = new File(TEST_FILE_PATH);
            if (file.createNewFile()) {
                // Write some initial data to the test file
                Dict testDict = new Dict(TEST_FILE_PATH);
                testDict.add("test", "meaning1;meaning2");
                testDict.add("test2", "meaning3;meaning4");
                testDict.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to delete the test dictionary file
    private void deleteTestDictionaryFile() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }
}
