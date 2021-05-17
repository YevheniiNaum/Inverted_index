
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


        System.out.println("check");//for debugging
    }

    private static void selectSizeOfData() {
        for (int i = 0; i < Indexer.N.length; i++) {
            Indexer.startIndex[i] = Indexer.N[i] / 50 * (Indexer.V - 1);
            Indexer.endIndex[i] = Indexer.N[i] / 50 * Indexer.V;
        }
    }


}
