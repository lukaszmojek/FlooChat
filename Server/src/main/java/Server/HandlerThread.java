package Server;
import org.apache.commons.lang.StringUtils;
import baza.*;
import java.io.*;
import java.net.Socket;
import java.util.*;


public class HandlerThread extends Thread {//wątek do obsługi klienta
    Baza b;//obiekt dostepu do bazy
    int idKlienta;
    private final Socket clientSocket;//gniazdo klienta
    private final ServerColections serverColections;//gniazdo serwera
    private String login = null;//login
    private OutputStream outputStream;//do pisania
    private HashSet<String> chatRoomSet = new HashSet<>();//set ktory przechowuje grupy chatu

    public HandlerThread(ServerColections serverColections, Socket clientSocket) { //przekazywanie instancji serverColection do kazdego Workera
        this.serverColections = serverColections;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {//metoda run watku
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {//obsługa klienta
         /*
        dwukierunkowa komunikacja
        */
        InputStream inputStream = clientSocket.getInputStream();// obiekt do czytania danych z klienta
        this.outputStream = clientSocket.getOutputStream();// obiekt do wysyłania danych do klienta

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));//czytanie linijka po linijce
        String line;//zmienna do przechowania
        while ( (line = reader.readLine()) != null) {//wczytywanie
            try {
                String[] tokens = StringUtils.split(line);//obiekt do przechowywania wczytanych linii
                if (tokens != null && tokens.length > 0) {//jezeli przesłano jakiekolwiek dane
                    String cmd = tokens[0];//pobierz pierwsze slowo
                    if ("logoff".equals(cmd)) {
                        handleLogoff();//obsluz wylogowanie
                        break;
                    } else if ("quit".equalsIgnoreCase(cmd)) {  //jesli napisales quit   to koniec czytania
                        clientSocket.close();;//wymuszone zamkniecie
                    } else if ("login".equalsIgnoreCase(cmd)) { //login
                        handleLogin(outputStream, tokens);//obsluz logowanie
                    } else if ("msgPriv".equalsIgnoreCase(cmd)) { //chce przeslac wiadomosc
                        String[] tekstMsg = StringUtils.split(line, null, 3);//wczytaj wiadomosc do obiektu tekstMsg...
                        handleMessagePrivate(tekstMsg);//... i obsluz ja
                    } else if ("msgHouse".equalsIgnoreCase(cmd)) { //chce przeslac wiadomosc
                        String[] tekstMsg = StringUtils.split(line, null, 4);//wczytaj wiadomosc do obiektu tekstMsg...
                        handleMessageHouse(tekstMsg);//... i obsluz ja
                    } else if ("getMsgHouse".equalsIgnoreCase(cmd)) { //chce przeslac wiadomosc
                        String[] tekstMsg = StringUtils.split(line, null, 2);//wczytaj wiadomosc do obiektu tekstMsg...
                        handleGetMessageHouse(tekstMsg);//... i obsluz ja
                    } else if ("reg".equalsIgnoreCase(cmd)) { //registration
                        handleRegistration(outputStream, tokens);//obsluz rejstracje
                    } else if ("delete".equalsIgnoreCase(cmd)) { //usun konto
                        handleDel(outputStream, tokens);//obsluz dane
                    } else if ("change".equalsIgnoreCase(cmd)) { //zmien dane
                        handleChange(outputStream, tokens);//obsluz dane
                    } else if ("addfriend".equalsIgnoreCase(cmd)) { //dodaj znajomego
                        handleAddFriend(outputStream, tokens);//obsluz dodawanie znajomego
                    } else if ("delfriend".equalsIgnoreCase(cmd)) { //usun znajomego
                        handleDelFriend(outputStream, tokens);//obsluz usuwanie znajomego
                    } else if ("getPassword".equalsIgnoreCase(cmd)) { //zmien haslo
                        handleReturnHaslo(outputStream, tokens);//obsluz zmiane hasla
                    } else if ("getData".equalsIgnoreCase(cmd)) {// dane klienta
                        handleReturnDane(outputStream, tokens);//obsluz dane
                    } else if ("getfriends".equalsIgnoreCase(cmd)) {//zwroc znajomych
                        handleReturnFriends(outputStream, tokens);
                    } else if ("getMsg".equalsIgnoreCase(cmd)) {// zwroc wiadomosc
                        handleGettingMessages(outputStream, tokens);
                    } else {
                        String msg = "Nieznana komenda " + cmd + "\n"; //inne to nieznane
                        outputStream.write(msg.getBytes());//wyslanie wiadomosci o nieznanej komendzie
                        outputStream.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(clientSocket!=null)clientSocket.close();//zamknij gniazdo klienta jesli jeszcze nie zamkniete
    }

    private void handleMessagePrivate(String[] tokens) throws  IOException{//obsluga wiadomosci private
        String odbiorca = tokens[1];//login odbiorcy
        String tresc = tokens[2];//tresc wiadomosci
        b=new Baza();

        List<HandlerThread> workerList = serverColections.getWorkerList();

        for (HandlerThread worker : workerList){
            if (b.getIdKlienta(odbiorca)!=0) {
                if (b.getIDZnajomych(b.getIdKlienta(login)).contains(b.getIdKlienta(odbiorca))) {
                    if (login.equalsIgnoreCase(worker.getLogin())) {
                        var odebrana = new Wiadomosc(tresc, b.getIdKlienta(login), new Date(),0);
                        b.dodajWiadomosc(b.getIdKlienta(odbiorca), odebrana);

                        var wyslana = new Wiadomosc(tresc, b.getIdKlienta(odbiorca), new Date(),1);
                        b.dodajWiadomosc(b.getIdKlienta(login), wyslana);

                        System.out.println("Wiadomosc zapisana \n");
                        break;
                    }

                    //zapis wiadomosci w bazach odbiorcy i nadawcy
                } else {
                   System.err.println("Wiadomosc nie zostala wyslana - nie jestescie znajomymi\n");
                }
            } else {
                System.err.println("Wiadomosc nie zostala wyslana - odbiorca nie istnieje\n");
            }
        }
        b.closeConnection();
    }

    private void handleMessageHouse(String[] tokens) throws  IOException{//obsluga wiadomosci domu
        String login = tokens[1];//login odbiorcy
        String dom = tokens[2];//tresc wiadomosci
        String tresc = tokens[3];//tresc wiadomosci
        b=new Baza();
        try {
            b.wyslijWiadomoscDomu(login, dom, tresc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        b.closeConnection();
    }

    private void handleGetMessageHouse(String[] tokens) throws  IOException{//obsluga wiadomosci domu
        String dom = tokens[1];

        b=new Baza();

        try {
            var wiadomosci = b.getWiadomosci(dom);
            outputStream.write(wiadomosci.size());
            outputStream.flush();

            for (var wiadomosc : wiadomosci) {
                outputStream.write((wiadomosc.login+": "+wiadomosc.tresc+"\n").getBytes());
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        b.closeConnection();
    }

    private void handleReturnHaslo(OutputStream outputStream, String[] tokens) throws IOException{ // przypomnienei hasloa
        b=new Baza();
        if(tokens.length == 4){
            String login = tokens[1];
            String imie = tokens[2];
            String nazwisko = tokens[3];

            if (b.checkIfLoginExist(login)) {//jezeli w bazie jest taki osobnik
                String haslo = b.getHasloKlienta(imie,nazwisko,login);
                if (haslo != null) {
                    String msg = "Haslo : " + haslo + " \n";//informacja o hasle
                    outputStream.write(msg.getBytes());
                    outputStream.flush();
                    System.out.println("Haslo : " + haslo + " \n");//pomocniczo w konsoli informacja o przypomnieniu hasla
                } else {
                    String msg = "Blad przy odzyskiwaniu hasla\n";
                    outputStream.write(msg.getBytes());
                    outputStream.flush();
                    System.err.println("Blad przy odzyskiwaniu hasla\n");
                }
            } else {
                String msg = "Uzytkownik o loginie " + login + " nie istnieje \n";
                outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
                outputStream.flush();
                System.err.println("Uzytkownik o loginie " + login + " nie istnieje \n");
            }
        }
        b.closeConnection();
    }

    private void handleDelFriend(OutputStream outputStream, String[] tokens) throws IOException { // usuniecie znajomego
        b = new Baza();
        if (tokens.length == 4) {
            String login = tokens[1];
            String password = tokens[2];
            String loginZnajomy = tokens[3]; //login znajomego

            if (b.logowanie(login, password)) {//jezeli w bazie jest taki osobnik
                if (b.checkIfLoginExist(loginZnajomy)) { // sprawdz czy jest taka osoba w bazie
                    if (b.getIDZnajomych(b.getIdKlienta(login)).contains(b.getIdKlienta(loginZnajomy))) {
                        String msg = "OK\n";
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                        System.out.println("Znajomy " + loginZnajomy + " został usuniety\n");
                        b.usunZnajomego(b.getIdKlienta(login), b.getIdKlienta(loginZnajomy));
                    } else {
                        String msg = "NIE\n";
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                        System.err.println("Uzytkownik o loginie ''" + loginZnajomy + "'' nie jest twoim znajomym\n");
                    }
                } else {
                    String msg = "BRAK\n";
                    outputStream.write(msg.getBytes());
                    outputStream.flush();
                    System.err.println("Uzytkownik o loginie ''" + loginZnajomy + "'' nie istnieje w bazie\n");
                }
            } else {
                String msg = "BLAD\n";
                outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
                outputStream.flush();
                System.err.println("Nieudane usuniecie znajomego: " + login + "\n");
            }

            b.closeConnection();
        }
    }

    private void handleAddFriend(OutputStream outputStream, String[] tokens) throws IOException{ // dodawnie znajomego
        b=new Baza();
        if(tokens.length == 4){
            String login = tokens[1];
            String password = tokens[2];
            String loginZnajomy = tokens[3]; //login znajomego

            if (b.logowanie(login, password)) {//jezeli w bazie jest taki osobnik
                if (b.checkIfLoginExist(loginZnajomy)) { // sprawdz czy jest taka osoba w bazie
                    if (b.getIDZnajomych(b.getIdKlienta(login)).contains(b.getIdKlienta(loginZnajomy))) {
                        String msg = "JUZ\n";
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                        System.out.println("Jestescie juz znajomymi z : " + loginZnajomy + "\n");
                    } else{
                        b.dodajZnajomego(b.getIdKlienta(login),b.getIdKlienta(loginZnajomy));
                        String msg = "OK\n";
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                        System.out.println("Znajomy " + loginZnajomy + " został dodany\n");
                    }
                } else {
                    String msg = "BRAK\n";
                    outputStream.write(msg.getBytes());
                    outputStream.flush();
                    System.err.println("Uzytkownik o loginie ''" + loginZnajomy + "'' nie istnieje w bazie\n");
                }

            } else {
                String msg = "BLAD\n";
                outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
                outputStream.flush();
                System.err.println("Nieudane dodanie znajomego: " + login+ "\n");
            }
        }
        b.closeConnection();
    }

    private void handleChange(OutputStream outputStream, String[] tokens)throws IOException { // oblsuga zmiany danych
        b=new Baza();
        if (tokens.length == 5) {//czy polecenie zawiera komende Change, login , haslo, co zmienic ,na co zmienic
            String login = tokens[1];
            String password = tokens[2];
            String toChange = tokens[3]; //co zmienic
            String forChange = tokens[4];//na co zmienic
            idKlienta = b.getIdKlienta(login);

            if (b.logowanie(login, password)) {//jezeli w bazie jest taki osobnik
                String msg = "OK\n";//informacja o zmianie danych
                outputStream.write(msg.getBytes());//przeslij potwierdzenie zalogowania
                outputStream.flush();
                System.out.println("Dane użytkownika zostały zmienione: " + login+ "\n");//pomocniczo w konsoli informacja o zmianie danych
                b.zmienDaneKlienta(toChange,forChange, idKlienta);
            } else {
                String msg = "error date\n";
                outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
                outputStream.flush();
                System.err.println("Nieudana zmiana danych: " + login+ "\n");
            }
        } else {
            String msg = "error date\n";
            outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
            outputStream.flush();
            System.err.println("error date\n" + login);
        }
        b.closeConnection();
    }

    private void handleDel(OutputStream outputStream, String[] tokens) throws IOException { // obsluga usuniecia konta
        b=new Baza();
        if (tokens.length == 3 ) {//czy polecenie zawiera komende date, login , haslo
            String login = tokens[1];
            String password = tokens[2];

            idKlienta = b.getIdKlienta(login);

            if (b.logowanie(login, password)) {//jezeli w bazie jest taki osobnik
                String msg = "OK\n";//informacja ze klient zostal usunietu
                outputStream.write(msg.getBytes());//przeslij potwierdzenie zalogowania
                outputStream.flush();
                System.out.println("Uzytkownik o takim loginie został usunięty: " + login+ "\n");//pomocniczo w konsoli informacja o usunieciu
                b.zamknijKonto(idKlienta);

            } else {
                String msg = "error delete\n";
                outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
                outputStream.flush();
                System.err.println("Nieudane usuniecie: " + login+ "\n");
            }
        } else {
            String msg = "error delete\n";
            outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
            outputStream.flush();
            System.err.println("error delete\n" + login);
        }
        b.closeConnection();
    }

    private void handleRegistration(OutputStream outputStream, String[] tokens) throws IOException{ //obsluga rejestracji klienta
        b=new Baza();
        if (tokens.length == 6) {//czy polecenie zawiera komende reg, login klienta i haslo klienta
            String imie = tokens[1];
            String nazwisko = tokens[2];
            String login = tokens[3];
            String password = tokens[4];
            String dom = tokens[5];

            if(b.checkIfLoginExist(login)){//jezeli w bazie jest taki osobnik
                String msg = "NIE\n";//informacja ze klient juz istnieje w bazie
                outputStream.write(msg.getBytes());//przeslij potwierdzenie zalogowania
                outputStream.flush();
                System.err.println("Uzytkownik o takim loginie juz istnieje: " + login+ "\n");//pomocniczo w konsoli informacja o bledzie
            } else {
                b.insertKlient(imie,nazwisko,login,password,dom);
                String msg = "TAK\n";//informacja ze klient juz istnieje w bazie
                outputStream.write(msg.getBytes());//przeslij potwierdzenie zalogowania
                outputStream.flush();
                System.err.println("Uzytkownik o takim loginie został stworzony: " + login+ "\n");//pomocniczo w konsoli informacja o bledzie
            }
        }
        b.closeConnection();
    }

    private void handleLogoff() throws IOException {//obsluga wylogowania
        clientSocket.close();
        serverColections.removeWorker(this);
        System.out.println("Wylogowal sie poprawnie "+login);
    }

    public String getLogin() {//zwraca login klienta
        return login;
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {//obsluga logowania klienta
        b=new Baza();
        if (tokens.length == 3) {//czy polecenie zawiera komende login, login klienta i haslo klienta
            String login = tokens[1];
            String password = tokens[2];

            if(b.logowanie(login,password)){//jezeli w bazie jest taki osobnik
                String msg = "Zalogowano\n";//wiadomosc
                outputStream.write(msg.getBytes());//przeslij potwierdzenie zalogowania
                outputStream.flush();
                this.login = login;
                System.out.println("Zalogowal sie poprawnie: " + login+ "\n");//pomocniczo w konsoli informacja kto sie zalogowal

                List<HandlerThread> workerList = serverColections.getWorkerList();//pobranie listy zalogowanych

            } else {//jezeli w bazie takiego osobnika nie ma to blad logowania
                String msg = "error login\n";
                outputStream.write(msg.getBytes());//wyslij klientowi informacje o bledzie
                outputStream.flush();
                System.err.println("Nieudane logowanie: " + login);
            }
        }
        b.closeConnection();
    }

    private void handleGettingMessages(OutputStream outputStream, String[] tokens) throws IOException {//pobiera i wysyła klientowi wiadomosci
        b = new Baza();
        if (tokens.length == 3)
        {
            String klient = tokens[1];//login odbiorcy
            String znajomy = tokens[2];//tresc wiadomosci

            int idKlienta=b.getIdKlienta(klient);
            int idZnajomy=b.getIdKlienta(znajomy);
            List<Wiadomosc> wiad = new ArrayList<Wiadomosc>();
            wiad=b.getWiadomosci(idKlienta, idZnajomy);
            int size = wiad.size();
            System.out.println("rozmiar tablicy: " + size) ;

            outputStream.write(size);
            outputStream.flush();

            String msg;

            for(int i=0; i<size;i++)
            {
                msg=wiad.get(i).toString();
                outputStream.write(msg.getBytes());
                outputStream.flush();
            }
        }

        b.closeConnection();
    }

    private void handleReturnDane(OutputStream outputStream, String[] tokens) throws IOException    {
        if(tokens.length==2)
        {
            b=new Baza();
            String login = tokens[1];
            int id=b.getIdKlienta(login);
            Klient klient=b.getDaneKlienta(id);
            String imie=klient.getImie()+ "\n";
            String nazwisko=klient.getNazwisko()+ "\n";
            String dom=klient.getDom()+ "\n";
            outputStream.write(imie.getBytes());
            outputStream.flush();
            outputStream.write(nazwisko.getBytes());
            outputStream.flush();
            outputStream.write(dom.getBytes());
            outputStream.flush();
        }
        b.closeConnection();
    }

    private void handleReturnFriends(OutputStream outputStream, String[] tokens) throws IOException    {
        if(tokens.length==2)
        {
            b=new Baza();
            String login = tokens[1];
            int id=b.getIdKlienta(login);

            List<Integer> idZnajomych=b.getIDZnajomych(id);

            int size=idZnajomych.size();

            outputStream.write(size);
            outputStream.flush();

            int idZnajomego;
            String emailZnajomego;

            for(int i=0; i<size; i++)
            {
                idZnajomego=idZnajomych.get(i);
                Klient znajomy=b.getDaneKlienta(idZnajomego);
                emailZnajomego=znajomy.getEmail() + "\n";
                outputStream.write(emailZnajomego.getBytes());
                outputStream.flush();
            }
        }
        b.closeConnection();
    }
}
