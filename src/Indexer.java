import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Indexer {

    /*Size of files*/
    private static final int[] N = {12500, 12500, 12500, 12500, 50000};
    /*Number of variable*/
    private static final int V = 20;
    /*start and end of required files*/
    private static final int[] startIndex = new int[N.length];
    private static final int[] endIndex = new int[N.length];

    /*Path to main directories*/

    public static void main(String[] args) {
        selectSizeOfData();
        File[] pathToDirections = initFiles();

        HashMap<String, ArrayList<String>> dictionary = new HashMap<>();

        buildIndex(pathToDirections, dictionary);
        printIndexByWord("BBC", dictionary);
        System.out.println("check");//for debugging
    }

    private static void selectSizeOfData() {
        for (int i = 0; i < Indexer.N.length; i++) {
            Indexer.startIndex[i] = Indexer.N[i] / 50 * (Indexer.V - 1);
            Indexer.endIndex[i] = Indexer.N[i] / 50 * Indexer.V;
        }
    }

    private static File[] initFiles() {
        return new File[]{
                new File("data//aclImdb//test//neg"),
                new File("data//aclImdb//test//pos"),
                new File("data//aclImdb//train//neg"),
                new File("data//aclImdb//train//pos"),
                new File("data//aclImdb//train//unsup")};
    }

    public static void buildIndex(File[] paths, HashMap<String, ArrayList<String>> dictionary) {
        for (File path : paths) {
            int numberOfPath = 0;
            File dir = path;

            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File item : files) {

                    String fileName = item.getName();

                    if (Integer.parseInt(fileName.replaceAll("_+\\d+.txt", "")) >= startIndex[numberOfPath]
                            && Integer.parseInt(fileName.replaceAll("_+\\d+.txt", "")) < endIndex[numberOfPath]) {
                        try (BufferedReader bufReader = new BufferedReader(new FileReader(item))) {
                            String line;
                            while ((line = bufReader.readLine()) != null) {
                                line = line.replaceAll("<br /><br />", "");


                                String[] words = line.split("\\s*(\\s|,|!|_|\\.)\\s*");

                                for (String word : words) {
                                    dictionary.computeIfAbsent(word, k -> new ArrayList<String>())
                                            .add(dir.getParent() + "\\" + dir.getName() + "\\" + item.getName());
                                }
                            }
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
            numberOfPath++;
        }
    }

    static void printIndexByWord(String word, HashMap<String, ArrayList<String>> dictionary) {
        ArrayList<String> array = null;
        if (dictionary.containsKey(word))
            array = dictionary.get(word);
        for (String search : array)
            System.out.println(search);
    }
}
