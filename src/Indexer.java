import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Indexer {

    private static final int NUMBER_THREADS = 4;
    public static ArrayList<File> allFiles = new ArrayList<File>();

    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> invertedIndex = new ConcurrentHashMap<>();
    public static ArrayList<String> stopWords = new ArrayList<>();

    public Indexer() throws InterruptedException {
        File[] pathToDirections = initFolders();
        File fileStopWords = new File("stopWords.txt");

        initStopWords(fileStopWords, stopWords);
        initAllFiles(pathToDirections);


        System.out.println("Building an index");
        double startTime, finalTime;
        startTime = System.nanoTime();
        parallelBuildIndex(pathToDirections, invertedIndex);
        finalTime = (System.nanoTime() - startTime) / 1000000000;
        System.out.println("Finished building the index\nTime of building: " + finalTime + " seconds\n");

    }

    private static File[] initFolders() {
        return new File[]{
                new File("data//aclImdb//test//neg"),
                new File("data//aclImdb//test//pos"),
                new File("data//aclImdb//train//neg"),
                new File("data//aclImdb//train//pos"),
                new File("data//aclImdb//train//unsup")};
    }

    private static void initAllFiles(File[] paths) {
        for (File path : paths) {
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                allFiles.addAll(Arrays.asList(files));
            }
        }
    }

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

    public static void parallelBuildIndex(File[] paths, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> invertedIndex) throws InterruptedException {
        IndexBuilder[] thread = new IndexBuilder[NUMBER_THREADS];

        for (int i = 0; i < NUMBER_THREADS; i++) {//разбиваем на потоки

            thread[i] = new IndexBuilder(allFiles, allFiles.size() / NUMBER_THREADS * i,
                    i == (NUMBER_THREADS - 1) ? allFiles.size() : allFiles.size() / NUMBER_THREADS * (i + 1), allFiles.size());
            thread[i].start();
        }
        //завершение потоков
        for (int i = 0; i < NUMBER_THREADS; i++) {
            thread[i].join();
        }

    }

    public ArrayList<String> searchFiles(String sentence) {
        sentence = sentence
                .replaceAll("[^A-Za-z0-9']", " ")
                .toLowerCase();

        String[] words = sentence.split("\\s*(\\s|,|!|_|\\.)\\s*");
        Set<String> firstToken = null;
        //добавляем файлы первового элемента, который не в стоп-словах в набор
        for (String word : words) {
            if (!stopWords.contains(word)) {
                firstToken = new HashSet<>(invertedIndex.get(word));
            }
        }

        ArrayList<String> result = null;
        if (firstToken != null) {
            //с помощью специальной функции retainAll() делаем пересечение каждого из множеств
            for (String word : words) {
                if (stopWords.contains(word)) continue;
                Set<String> tempSet = new HashSet<>(invertedIndex.get(word));
                firstToken.retainAll(tempSet);
            }

            //output
            result = new ArrayList<>(firstToken);
        } else {
            result = new ArrayList<>();
            result.add("EMPTY!!  (Maybe it was a stop word!)");
        }
        return result;
    }
}
