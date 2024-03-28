package server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Dict {
    private static ConcurrentHashMap<String, PriorityQueue<String>> dictionary;
    private final String filePath;
    Comparator<String> MEANINGQUEUECOMPARATOR = Comparator.comparingInt(String::length);

    public Dict(String filePath) {
        if(dictionary != null) {
            throw new IllegalStateException("Already initialized an instance of Dict");
        }

        this.filePath = filePath;
        dictionary = new ConcurrentHashMap<>();
        // Load current records from the file
        try (BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(
                             new FileInputStream(filePath)))) {
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
            ServerLogger.logGeneralErr(STR."Invalid file path ----\{e.getMessage()}");
            e.printStackTrace();
        } catch (IOException e) {
            ServerLogger.logGeneralErr(STR."File Reading fail ----\{e.getMessage()}");
            e.printStackTrace();
            try {
                File file = new File(filePath);
                if (file.createNewFile()) {
                    ServerLogger.logGeneralErr(STR."File created: \{file.getName()}");
                }
            } catch (IOException ioException) {
                ServerLogger.logGeneralErr(STR."An error occurred ----\{ioException.getMessage()}");
                //stop exception
            }
        }
    }

    public static void main(String[] args) {
        Dict dict = new Dict("dictionary.txt");
        System.out.println(dict.search("banana"));
        System.out.println(dict.add("appleLLL", "a fruit"));
        System.out.println(dict.update("appleLLL", "a kind of fruit"));
        dict.printDictionaryInfo();
        dict.close();
    }

    public Map<String, PriorityQueue<String>> getDictionary() {
        return Collections.unmodifiableMap(dictionary);
    }

    private void processLine(String line) {
        if (line == null) {
            return;
        }
        // Split the word and meanings by ":"
        String[] split = line.split(":");

        if (split.length != 2) {
            // Handle the case where the line does not have correct format
            ServerLogger.logGeneralErr("Invalid line format: " + line);
            return;
        }

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
        if (dictionary.containsKey(word)) {
            //TODO: Log to client
            return false;
        }
        if (meanings.isEmpty()) {
            //TODO: Log to client
            return false;
        }
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
                if (dictionary.get(word).contains(meaning)) {
                    continue;
                }
                meaningsQueue.add(meaning);
            }
        }
        // If the word doesn't exist, return false; otherwise update it in the dictionary, return true
        meaningsQueue.addAll(dictionary.get(word));
        return dictionary.replace(word, meaningsQueue) != null;
    }

    // Method to save dictionary content to file
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, PriorityQueue<String>> entry : dictionary.entrySet()) {
                writer.write(entry.getKey() + ":" + String.join(";", entry.getValue()));
                writer.newLine();
            }
        } catch (IOException e) {
            ServerLogger.logGeneralErr("Error saving to file: " + e.getMessage());
        }
    }

    void printDictionaryInfo() {
        System.out.println("Dictionary Information:");
        for (String word : dictionary.keySet()) {
            System.out.println("Word: " + word + ", Meanings: " + search(word));
        }
        System.out.println();
    }

    // Method to close the dictionary and save content to file
    public void close() {
        saveToFile();
    }
}
