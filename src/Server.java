import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server{
    public static final int PORT = 9090;
    public static ServerSocket serverSocket;
    private static Socket clientSocket; // сокет для общения
    private static BufferedReader messageRequest; // поток для чтения с консоли
    private static BufferedReader reader; // поток чтения из сокета
    private static BufferedWriter writer; // поток записи в сокет

    public static LinkedList<ServerSomthing> serverList = new LinkedList<>(); // список всех нитей

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started");
        Indexer indexer = new Indexer();
        try {
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerSomthing(socket, indexer)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
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