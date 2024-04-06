/**
 * @Author: 1378156 Yongchun Li
 */

package server;

import prtc.Response;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dict {
    private static ConcurrentHashMap<String, PriorityQueue<String>> dictionary;
    private final String filePath;
    Comparator<String> MEANINGQUEUECOMPARATOR = Comparator.comparingInt(String::length);

    public Dict(String filePath) {
        if (dictionary != null) {
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
            System.out.println(STR."Fail to read file: \{e.getMessage()}");
            System.out.println(STR."Creating file: \{filePath}");

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
        System.out.println("Dictionary init successfully!");
    }

//    public static void main(String[] args) {
//        Dict dict = new Dict("dictionary.txt");
////        System.out.println(dict.innerSearchsearch("banana"));
////        System.out.println(dict.add("appleLLL", "a fruit"));
//        System.out.println(dict.add("appleLLL", "a"));
//        System.out.println(dict.update("apple", "a kind of fruit"));
//        dict.printDictionaryInfo();
//        dict.close();
//    }

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
            ServerLogger.logGeneralErr(STR."Invalid line format: \{line}");
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


    private String innerSearch(String word) {
        PriorityQueue<String> q = dictionary.get(word);
        return q == null ? null : String.join(";", q);
    }

    public Response search(String word) {
        String result = innerSearch(word);
        return result == null ? new Response(false, STR."Word \{word} Not Found") :
                new Response(true, STR."Word \{word} Found, meanings: ", result);
    }

    public Response add(String word, String meanings) {
        if (meanings.isEmpty()) {
            return new Response(false, "Meanings cannot be empty");
        }
        // Use computeIfAbsent to atomically add the word and its meanings to the dictionary
        PriorityQueue<String> meaningsQueue = new PriorityQueue<>(MEANINGQUEUECOMPARATOR);
        String[] meaningsArray = meanings.split(";");
        if (meaningsArray.length == 0) {
            return new Response(false, "Meanings cannot be empty");
        }
        for (String meaning : meaningsArray) {
            if (!meaning.isEmpty()) {
                meaningsQueue.add(meaning);
            }
        }

        // Use computeIfAbsent to atomically add the word and its meanings to the dictionary
        return dictionary.computeIfAbsent(word, k -> meaningsQueue) == meaningsQueue ?
                new Response(true, STR."Word \{word} added successfully, meanings: ", innerSearch(word)) :
                new Response(false, STR."Word \{word} already exists, meanings: ", innerSearch(word));
    }


    public Response delete(String word) {
        // If the word doesn't exist, return false; otherwise delete it from the dictionary, return true
        PriorityQueue<String> result;
        try{
            result = dictionary.remove(word);
        }catch (NullPointerException e){
            ServerLogger.logGeneralErr(STR."Error deleting word, nullpointer: \{e.getMessage()}");
            return new Response(false, STR."Error deleting word, nullpointer: \{e.getMessage()}");
        }
        return result == null ? new Response(false, STR."Word \{word} Not Found, nothing to delete") :
                new Response(true, STR."Word \{word} deleted successfully, Previous meanings: ", String.join(";", result));
    }

    public Response update(String word, String meanings) {
        if (meanings.isEmpty()) {
            return new Response(false, "Meanings cannot be empty, update failed");
        }
        AtomicBoolean isNoNewMeaning = new AtomicBoolean(false);
        // update the meanings of the word in an atomic way by a lambda function
        PriorityQueue<String> updatedMeanings = dictionary.computeIfPresent(word, (_, oldValue) -> {

            //create a new priority queue to store the updated meanings
            PriorityQueue<String> newMeanings = new PriorityQueue<>(MEANINGQUEUECOMPARATOR);
            String[] newMeaningsArray = meanings.split(";");
            for (String newMeaning : newMeaningsArray) {
                if (!newMeaning.isEmpty() && !oldValue.contains(newMeaning)) {
                    //if the new meaning is not empty and not in the old meanings, add it to the new meanings
                    newMeanings.add(newMeaning);
                }
            }
            if (newMeanings.isEmpty()) {
                isNoNewMeaning.set(true);
            }
            // add the old meanings to the new meanings
            newMeanings.addAll(oldValue);
            return newMeanings;
        });

        if (updatedMeanings != null) {
            String resMeanings = String.join(";", updatedMeanings);
            if (isNoNewMeaning.get())
                return new Response(true, "No new meaning to update, everything remains: ", resMeanings);
            else return new Response(true, STR."Word \{word} updated successfully, New meanings: ", resMeanings);
        } else {
            return new Response(false, STR."Word \{word} Not Found, update failed");
        }
    }


    // Method to save dictionary content to file
    public void saveToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, PriorityQueue<String>> entry : dictionary.entrySet()) {
                writer.write(STR."\{entry.getKey()}:\{String.join(";", entry.getValue())}");
                writer.newLine();
            }
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
        try {
            saveToFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
