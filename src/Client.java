import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int PORT = 9090;
    private static Socket clientSocket; // сокет для общения
    private static BufferedReader reader; // поток чтения из сокета
    private static BufferedWriter writer; // поток записи в сокет

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", PORT);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                System.out.println(reader.readLine());
                System.out.println("Write word or sentence for searching files");

                while (true) {
                    Scanner sc = new Scanner(System.in);//для ввода в консоли
                    String line = sc.nextLine();
                    if (line.isEmpty()) {
                        System.out.println("You didn't write anything");
                    } else {
                        try {
                            writer.write(line + "\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int sizeOfResponse = Integer.parseInt(reader.readLine());
                        System.out.println("\nRESPONSE FROM SERVER:");
                        for (int i = 0; i < sizeOfResponse; i++) {
                            System.out.println(reader.readLine());
                        }
                    }
                }
            } finally { // в любом случае необходимо закрыть сокет и потоки
                System.out.println("Клиент был закрыт...");
                clientSocket.close();
                reader.close();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}