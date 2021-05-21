import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server{
    public static final int PORT = 9090;
    public static ServerSocket serverSocket;
    private static Socket clientSocket; // socket for speaking

    public static LinkedList<OneClientClass> serverList = new LinkedList<>(); // список всех нитей

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started");
        Indexer indexer = new Indexer();
        try {
            while (true) {
                // Blocked until a new connection is established
                Socket socket = server.accept();
                try {
                    // add a new connection to the list
                    serverList.add(new OneClientClass(socket, indexer));
                } catch (IOException e) {
                    // If it fails, close the socket,
                    // otherwise, the thread will close it on exit:
                    socket.close();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.close();
        }
    }
}