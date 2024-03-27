package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Dict {

    private final ConcurrentHashMap<String, PriorityQueue<String>> dictionary;
    Comparator<String> MEANINGQUEUECOMPARATOR = Comparator.comparingInt(String::length);

    /**
     * Construct the initial dictionary
     *
     * @param filePath the file of initial dictionary inputs
     */
    public Dict(String filePath) {
        dictionary = new ConcurrentHashMap<>();
        // Load current records from the file
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(
                             Objects.requireNonNull(getClass().getResourceAsStream(filePath))))) {
            String line;
            //Each line store one and only one word and its meanings
            while ((line = bufferedReader.readLine()) != null) {
                // Split the word and meanings by ":"
                String[] split = line.split(":");
                PriorityQueue<String> meaningsQueue = new PriorityQueue<>(MEANINGQUEUECOMPARATOR);
                // Split the meanings by ";"
                String[] meaningsArray = split[1].split(";");
                for (String meaning : meaningsArray) {
                    if (!meaning.isEmpty()) {
                        meaningsQueue.add(meaning);
                    }
                }
                dictionary.put(split[0], meaningsQueue);
            }
        } catch (NullPointerException e) {
            ServerLogger.logGeneralErr("Invalid file path ----" + e.getMessage());
        } catch (IOException e) {
            ServerLogger.logGeneralErr("File Reading fail ----" + e.getMessage());
        }
    }

    /**
     * Search a word in the dictionary
     *
     * @param word the word to search
     * @return the meanings of this word, or null if not exist
     */
    public String search(String word) {
        System.out.println("Now searching: " + word);
        PriorityQueue<String> q = dictionary.get(word);
        if (q == null) {
            return null;
        }
        return String.join(";", q);
    }

    /**
     * Add a new word to this dictionary. Check if it already exists
     *
     * @param word     the word to add
     * @param meanings the meaning of the word. Should be non-empty
     * @return whether successfully added or the word already exists
     */
    public boolean add(String word, String meanings) {
        System.out.println("Now adding: " + word);
        PriorityQueue<String> meaningsQueue = new PriorityQueue<>(MEANINGQUEUECOMPARATOR);
        String[] meaningsArray = meanings.split(";");
        for (String meaning : meaningsArray) {
            if (!meaning.isEmpty()) {
                meaningsQueue.add(meaning);
            }
        }
        // If the word already exists, return false; otherwise add it to the dictionary, return true
        return dictionary.putIfAbsent(word, meaningsQueue) == null;
    }

    /**
     * Delete a word from the dictionary. Check if it exists
     *
     * @param word the word to delete
     * @return whether successfully deleted or the word doesn't exist
     */
    public boolean delete(String word) {
        System.out.println("Now deleting: " + word);
        // If the word doesn't exist, return false; otherwise delete it from the dictionary, return true
        return dictionary.remove(word) != null;
    }

    /**
     * Edit an existing word's meanings.
     *
     * @param word     the word to edit
     * @param meanings the new meanings
     * @return whether successfully updated or the word doesn't exist
     */
    public boolean update(String word, String meanings) {
        System.out.println("Now updating: " + word);
        PriorityQueue<String> meaningsQueue = new PriorityQueue<>(MEANINGQUEUECOMPARATOR);
        String[] meaningsArray = meanings.split(";");
        for (String meaning : meaningsArray) {
            if (!meaning.isEmpty()) {
                meaningsQueue.add(meaning);
            }
        }
        // If the word doesn't exist, return false; otherwise update it in the dictionary, return true
        return dictionary.replace(word, meaningsQueue) != null;
    }
}
