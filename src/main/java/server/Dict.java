package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Dict {

    private final ConcurrentHashMap<String, PriorityQueue<String>> dictionary;
    public Map<String, PriorityQueue<String>> getDictionary() {
        return Collections.unmodifiableMap(dictionary);
    }

    Comparator<String> MEANINGQUEUECOMPARATOR = Comparator.comparingInt(String::length);

    public Dict(String filePath) {
        dictionary = new ConcurrentHashMap<>();
        // Load current records from the file
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(
                             Objects.requireNonNull(getClass().getResourceAsStream(filePath))))) {
            String line;

            // Read the first line separately
            line = bufferedReader.readLine();
            if (line == null) {
                ServerLogger.logGeneralErr("Empty Record File!");
                return;
            }
            processLine(line);

            //Each line store one and only one word and its meanings
            while ((line = bufferedReader.readLine()) != null) {
                processLine(line);
            }
        } catch (NullPointerException e) {
            ServerLogger.logGeneralErr("Invalid file path ----" + e.getMessage());
        } catch (IOException e) {
            ServerLogger.logGeneralErr("File Reading fail ----" + e.getMessage());
        }
    }

    private void processLine(String line) {
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


    public String search(String word) {
//        System.out.println("Now searching: " + word);
        PriorityQueue<String> q = dictionary.get(word);
        if (q == null) {
            return null;
        }
        return String.join(";", q);
    }

    public boolean add(String word, String meanings) {
//        System.out.println("Now adding: " + word);
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

    public boolean delete(String word) {
//        System.out.println("Now deleting: " + word);
        // If the word doesn't exist, return false; otherwise delete it from the dictionary, return true
        return dictionary.remove(word) != null;
    }

    public boolean update(String word, String meanings) {
//        System.out.println("Now updating: " + word);
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
