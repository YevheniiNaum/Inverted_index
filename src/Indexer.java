import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Indexer {
    //Number of threads that will build indexes
    private static final int NUMBER_THREADS = 3;

    public static ArrayList<File> allFiles = new ArrayList<File>();
    //a map that stores a key-word and a value - an array of files that contain a word
    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> invertedIndex = new ConcurrentHashMap<>();
    //An array of stop words
    public static ArrayList<String> stopWords = new ArrayList<>();

    public Indexer() throws InterruptedException {
        File[] pathToDirections = initFolders();
        File fileStopWords = new File("..\\..\\..\\stopWords.txt");

        initStopWords(fileStopWords, stopWords);
        initAllFiles(pathToDirections);


        System.out.println("Building an index");
        double startTime, resultOfWork;
        startTime = System.nanoTime();
        parallelBuildIndex(pathToDirections, invertedIndex);
        resultOfWork = (System.nanoTime() - startTime) / 1000000000;            //index build time
        System.out.println("Finished building the index\nTime of building: " + resultOfWork + " seconds\n");

    }

    //a function that returns an array of directories
    private static File[] initFolders() {
        return new File[]{
                new File("..\\..\\..\\data//aclImdb//test//neg"),
                new File("..\\..\\..\\data//aclImdb//test//pos"),
                new File("..\\..\\..\\data//aclImdb//train//neg"),
                new File("..\\..\\..\\data//aclImdb//train//pos"),
                new File("..\\..\\..\\data//aclImdb//train//unsup")};
    }

    //a function that puts all files into one array
    private static void initAllFiles(File[] paths) {
        for (File path : paths) {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                allFiles.addAll(Arrays.asList(files));
            }
        }
    }

    //loading a file of stop words into array
    static void initStopWords(File file, ArrayList<String> arr) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                arr.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //A main function that build index
    public static void parallelBuildIndex(File[] paths, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> invertedIndex) throws InterruptedException {
        //creating an array of IndexBuilder class which will build index
        IndexBuilder[] thread = new IndexBuilder[NUMBER_THREADS];

        //splitting an array of files for threads
        for (int i = 0; i < NUMBER_THREADS; i++) {//разбиваем на потоки
            thread[i] = new IndexBuilder(allFiles, allFiles.size() / NUMBER_THREADS * i,
                    i == (NUMBER_THREADS - 1) ? allFiles.size() : allFiles.size() / NUMBER_THREADS * (i + 1), allFiles.size());
            thread[i].start();
        }
        //waiting for finish of threads
        for (int i = 0; i < NUMBER_THREADS; i++) {
            thread[i].join();
        }

    }

    //a function that returns a list of files that contain the words that were written for the search
    public ArrayList<String> searchFiles(String sentence) {
        sentence = sentence
                .replaceAll("[^A-Za-z0-9']", " ")
                .toLowerCase();

        String[] words = sentence.split("\\s*(\\s|,|!|_|\\.)\\s*");
        Set<String> firstToken = null;

        //add files of the first element that is not in the stop words to the set
        for (String word : words) {
            if (!stopWords.contains(word) && invertedIndex.containsKey(word)) {
                firstToken = new HashSet<>(invertedIndex.get(word));
                break;
            }
        }

        //result array
        ArrayList<String> result = null;
        //if the first set is not empty
        if (firstToken != null) {
            //using the special function retainAll () we make the intersection of each of the sets
            for (String word : words) {
                if (stopWords.contains(word)) continue;
                Set<String> tempSet = new HashSet<>(invertedIndex.get(word));
                firstToken.retainAll(tempSet);
            }

            result = new ArrayList<>(firstToken);

        } else {
            result = new ArrayList<>();
            result.add("EMPTY!!  (Maybe it was a stop word!)");
        }
        return result;
    }
}
