package Server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8818;
        ServerColections serverColections = new ServerColections(port);
        serverColections.start();
    }
}
