import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Indexer {

    /*Size of files*/
    //private static final int[] N = {12500, 12500, 12500, 12500, 50000};
    /*Number of variable*/
    //private static final int V = 20;
    /*start and end of required files*/
    //private static final int[] startIndex = new int[N.length];
    //private static final int[] endIndex = new int[N.length];

    /*Path to main directories*/

    public static void main(String[] args) {
        //selectSizeOfData();

        File[] pathToDirections = initFiles();
        File fileStopWords = new File("stopWords.txt");

        ArrayList<String> stopWords = new ArrayList<>();
        HashMap<String, ArrayList<String>> dictionary = new HashMap<>();

        initStopWords(fileStopWords, stopWords);
        buildIndex(pathToDirections, dictionary);
        searchFiles("i love films", dictionary, stopWords);
        System.out.println("CHECKING");
        searchFiles("me", dictionary, stopWords);
        System.out.println("check");//for debugging
    }

//    private static void selectSizeOfData() {
//        for (int i = 0; i < Indexer.N.length; i++) {
//            Indexer.startIndex[i] = Indexer.N[i] / 50 * (Indexer.V - 1);
//            Indexer.endIndex[i] = Indexer.N[i] / 50 * Indexer.V;
//        }
//    }

    private static File[] initFiles() {
        return new File[]{
                new File("data//aclImdb//test//neg"),
                new File("data//aclImdb//test//pos"),
                new File("data//aclImdb//train//neg"),
                new File("data//aclImdb//train//pos"),
                new File("data//aclImdb//train//unsup")};
    }

    static void initStopWords(File file, ArrayList<String> arr) {
        try (BufferedReader bufReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufReader.readLine()) != null) {
                arr.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildIndex(File[] paths, HashMap<String, ArrayList<String>> dictionary) {
        for (File path : paths) {
            int numberOfPath = 0;
            File dir = path;

            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File item : files) {

                    String fileName = item.getName();

//                    if (Integer.parseInt(fileName.replaceAll("_+\\d+.txt", "")) >= startIndex[numberOfPath]
//                            && Integer.parseInt(fileName.replaceAll("_+\\d+.txt", "")) < endIndex[numberOfPath]) {
                        try (BufferedReader bufReader = new BufferedReader(new FileReader(item))) {
                            String line;
                            while ((line = bufReader.readLine()) != null) {
                                line = line.toLowerCase();
                                line = line
                                        .replaceAll("<br /><br />", "")
                                        .replaceAll("[^A-Za-z0-9]", " ");


                                String[] words = line.split("\\s*(\\s|,|!|_|\\.)\\s*");

                                for (String word : words) {
                                    dictionary.computeIfAbsent(word, k -> new ArrayList<String>())
                                            .add(dir.getParent() + "\\" + dir.getName() + "\\" + item.getName());
                                    Set<String> set = new HashSet<>(dictionary.get(word));//удаление дубликатов
                                    dictionary.get(word).clear();
                                    dictionary.get(word).addAll(set);

                                }
                            }
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
//                    }
                }
            }
            numberOfPath++;
        }
    }


    private static void searchFiles(String sentence, HashMap<String, ArrayList<String>> dictionary, ArrayList<String> stopWords) {
        sentence = sentence
                .replaceAll("[^A-Za-z0-9']", " ")
                .toLowerCase();

        String[] words = sentence.split("\\s*(\\s|,|!|_|\\.)\\s*");
        Set<String> firstToken = null;
        //добавляем файлы первового элемента, который не в стоп-словах в набор
        for(String word: words){
            if(!stopWords.contains(word)){
                firstToken = new HashSet<>(dictionary.get(word));
            }
        }


        if(firstToken!=null){
            //с помощью специальной функции retainAll() делаем пересечение каждого из множеств
            for (String word : words) {
                if (stopWords.contains(word)) continue;
                Set<String> tempSet = new HashSet<>(dictionary.get(word));
                firstToken.retainAll(tempSet);
            }


            //output
            ArrayList<String> result = new ArrayList<>(firstToken);
            for (String s : words) {
                    System.out.print(s + " ");
            }
            System.out.println();
            for (String s : words) {
                if(!stopWords.contains(s)){
                    System.out.print(s + " ");
                }
            }
            System.out.println(" (without stop words)\n");
            for (String s : result) {
                System.out.println(s);
            }
        }else{
            for (String s : words) {
                System.out.print(s + " ");
            }
            System.out.println("\nEMPTY!!\nMaybe it was a stop word!");
        }

    }
}
