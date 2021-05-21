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

//        startTime = System.nanoTime();
//        buildIndex(pathToDirections, invertedIndex);
//        finalTime = (System.nanoTime() - startTime) / 1000000;
//        System.out.println("Finished building the index\nTime of building: " + finalTime + "\n");

        //searchFiles("i love films", invertedIndex, stopWords);
        //searchFiles("me", invertedIndex, stopWords);
        //searchFiles("will never care who lives or dies", invertedIndex, stopWords);
        //System.out.println("check");//for debugging
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

    public static void buildIndex(File[] paths, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> invertedIndex) {
        for (File path : paths) {
            File dir = path;

            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File item : files) {
                    try (BufferedReader br = new BufferedReader(new FileReader(item))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            line = line.toLowerCase();
                            line = line
                                    .replaceAll("<br /><br />", "")
                                    .replaceAll("[^A-Za-z0-9]", " ");


                            String[] words = line.split("\\s*(\\s|,|!|_|\\.)\\s*");

                            for (String word : words) {
                                invertedIndex.computeIfAbsent(word, k -> new ConcurrentLinkedQueue<String>())
                                        .add(dir.getParent() + "\\" + dir.getName() + "\\" + item.getName());
                                Set<String> set = new HashSet<>(invertedIndex.get(word));//удаление дубликатов
                                invertedIndex.get(word).clear();
                                invertedIndex.get(word).addAll(set);

                            }
                        }
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
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

    public  ArrayList<String> searchFiles(String sentence) {
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
//            for (String s : words) {
//                System.out.print(s + " ");
//            }
//            System.out.println();
//            for (String s : words) {
//                if (!stopWords.contains(s)) {
//                    System.out.print(s + " ");
//                }
//            }
//            System.out.println(" (without stop words)\n");
//            for (String s : result) {
//                System.out.println(s);
//            }

        } else {
            result = new ArrayList<>();
            result.add("EMPTY!!  (Maybe it was a stop word!)");
        }
        return result;
    }
}
