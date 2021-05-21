import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    public static final int PORT = 9090;
    public static ServerSocket serverSocket;
    private static Socket clientSocket; // сокет для общения
    private static BufferedReader messageRequest; // поток для чтения с консоли
    private static BufferedReader reader; // поток чтения из сокета
    private static BufferedWriter writer; // поток записи в сокет

    public static void main(String[] args) {
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Started");
            Indexer indexer = new Indexer();
            while(true){
                clientSocket =serverSocket.accept();
                try{
                    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    try{
                        writer.write("You connected to server!" + "\n");
                        writer.flush();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (true) { // постоянно смотрим на входящие данные с сервера и если они есть, выводим
                        String messageRequest = reader.readLine();
                        if (messageRequest!= null) {
                            System.out.println("REQUEST FROM USER: " + messageRequest);
                            //searchRequest(request);
                        }
                    }

                }catch (IOException e) {
                    System.out.println(e);
                    clientSocket.close();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}