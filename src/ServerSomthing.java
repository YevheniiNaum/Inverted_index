import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class ServerSomthing extends Thread {

    // кроме него - клиент и сервер никак не связаны
    private Socket socket;
    private Indexer indexer;
    private BufferedReader reader; // поток чтения из сокета
    private BufferedWriter writer; // поток записи в сокет

    public ServerSomthing(Socket socket, Indexer indexer) throws IOException {
        this.socket= socket;
        this.indexer = indexer;
        // сокет, через который сервер общается с клиентом,
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
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
                        sendMess(Integer.toString(response.size()));//отправляем размерность
                        for(String s: response){//отправляем по-элементно
                            sendMess(s);
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void sendMess(String msg) {
        try {
            writer.write(msg + "\n");
            writer.flush();
        } catch (IOException ignored) {}
    }
}