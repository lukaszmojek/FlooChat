package application;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatClient {

    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>(); // s�uchacze kto jest online

    public ChatClient(String serverName , int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }


    public String deleteAccount(String login, String password) throws IOException {
        String cmd = "delete " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
        String response = bufferedIn.readLine();
        return response;
    }

    public String changeData(String login, String haslo, String co_zmienic, String na_co) throws IOException {

        String cmd = "change " + login + " " + haslo + " " + co_zmienic + " " + na_co + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();

        String response = bufferedIn.readLine();

        return response;
    }

    public List<String> getFriends(String login) throws IOException {
        String cmd = "getfriends " + login + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();

        List<String> znajomi = new ArrayList<String>();

        int ilosc=bufferedIn.read();
        String email;

        for(int i=0; i<ilosc; i++)
        {
            email=bufferedIn.readLine();
            znajomi.add(email);
        }

        return znajomi;
    }

    public List<String> getMessage(String loginKlient, String loginZnajomy) throws IOException {
        String cmd = "getMsg " + loginKlient + " " + loginZnajomy + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
        List<String> wiadomosci = new ArrayList<String>();
        String wiadomosc;
        int ilosc=bufferedIn.read();
        System.out.println("Rozmiar tablicy : " + ilosc);
        for(int i=0; i<ilosc; i++)
        {
            wiadomosc=bufferedIn.readLine();
            wiadomosci.add(wiadomosc);
        }

        return wiadomosci;
    }

    public List<String> getMessage(String dom) throws IOException {
        String cmd = "getMsgHouse " + dom + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();

        List<String> wiadomosci = new ArrayList<String>();
        String wiadomosc;

        int ilosc=bufferedIn.read();
        System.out.println("Rozmiar tablicy : " + ilosc);

        for(int i=0; i<ilosc; i++)
        {
            wiadomosc=bufferedIn.readLine();
            wiadomosci.add(wiadomosc);
        }

        return wiadomosci;
    }

    public void sendMessage(String loginOdbiorcy, String tresc) throws IOException
    {
        String cmd = "msgPriv " + loginOdbiorcy + " " + tresc + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
    }

    public void sendMessageHouse(String login, String dom, String tresc) throws IOException
    {
        String cmd = "msgHouse " + login + " " + dom + " " + tresc + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
    }

    public String deleteFriend(String login, String haslo, String loginZnajomy) throws IOException {
        String cmd= "delfriend " + login + " " + haslo + " " + loginZnajomy + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();

        String response=bufferedIn.readLine();

        return response;
    }

    public String addFriends(String login, String haslo, String loginZnajomy) throws IOException
    {
        String cmd = "addfriend " + login + " " + haslo + " " + loginZnajomy + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
        String response=bufferedIn.readLine();
        return response;
    }

    public void getData(String login) throws IOException, InterruptedException
    {
        String cmd = "getData " + login + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();

        String imie = bufferedIn.readLine();
        String nazwisko = bufferedIn.readLine();
        String dom = bufferedIn.readLine();

        Main.user.imie=imie;
        Main.user.nazwisko=nazwisko;
        Main.user.dom=dom;
    }

    public void logoff() throws IOException{//wylogowanie
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());//wyslij serwerowi komende o wylogowanie
        serverOut.flush();
        String response = bufferedIn.readLine();//czekaj na potwierdzenie wylogowania
        System.out.println("Odpowiedz serwera: " + "''"+response+"''");//wyswietl odpowiedz klientowi
    }

    public boolean login(String login, String password) throws IOException {//logowanie
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());//wyslanie komendy logowania do serwera
        serverOut.flush();
        String response = bufferedIn.readLine();//czekaj na potwierdzenie logowania lub blad
        System.out.println("Odpowiedz serwera: " + "''"+response+"''");

        if ("Zalogowano".equalsIgnoreCase(response)) {//jesli serwer zwroci zalogowano to zako�cz i zwroc true
            return true;
        }

        return false;
    }

    public String recoverPassword(String email, String name, String lastName) throws IOException
    {
        String cmd = "getPassword " + email + " " + name + " " + lastName + "\n";
        serverOut.write(cmd.getBytes());
        serverOut.flush();
        String response = bufferedIn.readLine();

        return response;
    }

    public String register(String name, String lastName, String email, String password, String house) throws IOException
    {
        String cmd = "reg " + name + " " + lastName + " " + email + " " + password + " " + house +"\n";

        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        return response;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName,serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void addUserStatusListener(UserStatusListener listener){
        userStatusListeners.add(listener);
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.online(login);
        }
    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners){
            listener.offline(login);
        }
    }
}




