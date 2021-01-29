package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerColections extends Thread {
    private final int serverPort;

    private ArrayList<HandlerThread> workerList = new ArrayList<>();

    public ServerColections(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<HandlerThread> getWorkerList() {
        return workerList;
    }

    @Override
    public void run() {


        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);//tworzenie socketu
            while(true) {
                System.out.println("Czekam na polaczenie");
                Socket clientSocket = serverSocket.accept();//akceptuje nowe połączenie
                System.out.println("Zaakceptowano polaczenie od " + clientSocket);
                HandlerThread worker = new HandlerThread(this, clientSocket);// tworzenie nowego wątku dla każdego klienta
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(HandlerThread handlerThread) {
        workerList.remove(handlerThread);
    }
}
