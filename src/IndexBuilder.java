import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

//class for index building
public class IndexBuilder extends Thread {
    ArrayList<File> allFiles; //array of all files which created in Indexer
    //parameters necessary for the stream to find the range of its processing
    int startIndex;
    int endIndex;
    int size;

    public IndexBuilder(ArrayList<File> allFiles, int startIndex, int endIndex, int size) {
        this.allFiles = allFiles;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.size = size;
    }

    @Override
    public void run() {
        for(int i = startIndex; i< endIndex; i++){
            //trying to read each file
            try (BufferedReader br = new BufferedReader(new FileReader(allFiles.get(i)))){
                String line;
                while ((line = br.readLine()) != null) {
                    //get rid of uppercase
                    line = line.toLowerCase();
                    //get rid of unnecessary symbols
                    line = line
                            .replaceAll("<br /><br />", "")
                            .replaceAll("[^A-Za-z0-9]", " ");

                    //split a sentence into tokens
                    String[] words = line.split("\\s*(\\s|,|!|_|\\.)\\s*");

                    for (String word : words) {
                        //add a new key to the map, if there is none
                        //add a new value to the map when it is in the file
                        Indexer.invertedIndex.computeIfAbsent(word, k -> new ConcurrentLinkedQueue<String>())
                                .add(String.valueOf(allFiles.get(i)));
                        //get rid of duplicates (one word several times in one file)
                        Set<String> concurrentHashSet = new HashSet<>(Indexer.invertedIndex.get(word));
                        Indexer.invertedIndex.get(word).clear();
                        Indexer.invertedIndex.get(word).addAll(concurrentHashSet);

                    }
                }
            }catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
