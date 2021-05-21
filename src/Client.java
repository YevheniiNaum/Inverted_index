import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int PORT = 9090;
    private static Socket clientSocket; // socket for speaking
    private static BufferedReader reader; // stream of reading from socket
    private static BufferedWriter writer; // stream of writing to socket

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", PORT);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                //accept connection message
                System.out.println(reader.readLine());
                System.out.println("Write word or sentence for searching files");

                while (true) {
                    //print the request
                    Scanner sc = new Scanner(System.in);
                    String line = sc.nextLine();
                    if (line.isEmpty()) {
                        System.out.println("You didn't write anything");
                    } else {
                        //send request to server
                        try {
                            writer.write(line + "\n");
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //size of data which server sent
                        int sizeOfResponse = Integer.parseInt(reader.readLine());
                        //element-by-element output of the server message
                        System.out.println("\nRESPONSE FROM SERVER:");
                        for (int i = 0; i < sizeOfResponse; i++) {
                            System.out.println(reader.readLine());
                        }
                    }
                }
            } finally {
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