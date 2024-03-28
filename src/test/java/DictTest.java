import org.junit.Before;
import org.junit.Test;
import server.Dict;
import server.ServerLogger;

import static org.junit.Assert.*;

public class DictTest {

    ServerLogger serverLogger;

    private Dict dict;

    @Before
    public void setUp() {
        dict = new Dict("test_dictionary.txt"); // Assuming a test dictionary file exists
    }

    @Test
    public void testSearch() {
        String word = "test";
        assertNull(dict.search(word)); // Word doesn't exist in the dictionary
        dict.add(word, "meaning1;meaning2");
        assertEquals("meaning1;meaning2", dict.search(word)); // Word exists, test search functionality
        printDictionaryInfo();
    }

    @Test
    public void testAdd() {
        String word = "test";
        assertTrue(dict.add(word, "meaning1;meaning2")); // Add new word to dictionary
        assertFalse(dict.add(word, "meaning3")); // Word already exists, should return false
        printDictionaryInfo();
    }

    @Test
    public void testDelete() {
        String word = "test";
        assertFalse(dict.delete(word)); // Word doesn't exist, should return false
        dict.add(word, "meaning1");
        assertTrue(dict.delete(word)); // Word exists, should return true after deletion
        assertNull(dict.search(word)); // Word should no longer exist in the dictionary
        printDictionaryInfo();
    }

    @Test
    public void testUpdate() {
        String word = "test";
        assertFalse(dict.update(word, "meaning1")); // Word doesn't exist, should return false
        dict.add(word, "meaning1");
        assertTrue(dict.update(word, "newmeaning")); // Update existing word, should return true
        assertEquals("newmeaning", dict.search(word)); // Verify word has been updated
        printDictionaryInfo();
    }

    // Helper method to print dictionary information
    private void printDictionaryInfo() {
        System.out.println("Dictionary Information:");
        for (String word : dict.getDictionary().keySet()) {
            System.out.println("Word: " + word + ", Meanings: " + dict.search(word));
        }
        System.out.println();
    }
}
