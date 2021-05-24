import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class OneClientClass extends Thread {

    // кроме него - клиент и сервер никак не связаны
    private Socket socket;
    private Indexer indexer;
    private BufferedReader reader; // stream of reading f    rom socket
    private BufferedWriter writer; // stream of writing socket


    public OneClientClass(Socket socket, Indexer indexer) throws IOException {
        this.socket= socket;
        this.indexer = indexer;

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // init run()
    }

    @Override
    public void run() {
        try {
            sendMess("You connected to server!");
            System.out.println("A new client has been added");


            while (true) { // постоянно смотрим на входящие данные с сервера и если они есть, выводим
                String messageRequest = reader.readLine();

                if (messageRequest != null) {
                    System.out.println("REQUEST FROM USER: " + messageRequest);
                    ArrayList<String> response = indexer.searchFiles(messageRequest);
                    if(response!=null){
                        // send dimension
                        sendMess(Integer.toString(response.size()));
                        // send element-by-element
                        for(String s: response){
                            sendMess(s);
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
    //a function for sending a message to client
    private void sendMess(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException ignored) {}
    }
}