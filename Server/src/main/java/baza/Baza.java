package baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Baza {
	public static final String driver="org.sqlite.JDBC";
	public static final String dburl = "jdbc:sqlite:komunikator.db";

	private Connection conn;
	private Statement stat;

	public Baza() {
		try {
			Class.forName(Baza.driver);
		} catch (ClassNotFoundException e) {
			System.err.println("Brak sterownika jdbc");
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(dburl,"root","1234");
			stat = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean insertKlient(String imie,String nazwisko,String email,String haslo,String dom) {
		try {
			ResultSet result=stat.executeQuery("SELECT id_klienta FROM klienci WHERE email='"+email+"';");
			int i=0;
			if(result.next())i=result.getInt("id_klienta");

			if(i!=0) {
				System.out.println("Podany adres email jest ju� w bazie!");
				return false;//jesli taki email jest juz w bazie to zakoncz
			}

			PreparedStatement prepstmt=conn.prepareStatement("insert into klienci values (NULL,?,?,?,?,?);");
			prepstmt.setString(1, imie);
			prepstmt.setString(2, nazwisko);
			prepstmt.setString(3, email);
			prepstmt.setString(4, haslo);
			prepstmt.setString(5, dom);
			prepstmt.execute();

			int idNowegoKlienta;
			prepstmt=conn.prepareStatement("SELECT id_klienta FROM klienci WHERE email='"+email+"';");
			ResultSet wynik=prepstmt.executeQuery();

			if(wynik.next()) {
				idNowegoKlienta=wynik.getInt("id_klienta");//pobranie id klienta do stworzenia tabel

				String tworzznajomych="CREATE TABLE IF NOT EXISTS znajomi"+idNowegoKlienta+" (id_znajomego INTEGER PRIMARY KEY NOT NULL, "
						+ "data_znajomosci DATE);";
				prepstmt=conn.prepareStatement(tworzznajomych);
				prepstmt.execute();//stworzenie tabeli ze znajomymi klienta
				String tworzwiadomosci="CREATE TABLE IF NOT EXISTS wiadomosci"+idNowegoKlienta+" (id_wiadomosci INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
						+ "tresc VARCHAR(200), id_znajomego INTEGER NOT NULL, data_wyslania DATE, czy_wyslana INTEGER);";
				prepstmt=conn.prepareStatement(tworzwiadomosci);
				prepstmt.execute();//stworzenie tabeli z wiadomosciami
			}
			else return false;
		}catch(SQLException e) {
			e.printStackTrace();
			System.err.println("Blad przy tworzeniu profilu");
			return false;
		}
		return true;
	}

	public boolean zamknijKonto(int id) {//usuwanie konta klienta wraz z jego wiadomosciami i lista znajomych
		try {
			List<Integer> idznajomych= new ArrayList<Integer>();//lista do przechowania id znaomych u ktorych trzeba usunac klienta
			ResultSet result=stat.executeQuery("SELECT id_znajomego FROM znajomi"+id+";");
			while(result.next()) {//pobranie numerow id znajomych
				idznajomych.add(result.getInt("id_znajomego"));
			}

			for(int i=0;i<idznajomych.size();i++) {//usuwanie informacji o znajomo�ci z tabel znajomych
				PreparedStatement prepstmt=conn.prepareStatement("DELETE FROM znajomi"+idznajomych.get(i)+" WHERE id_znajomego="+id+";");//pobierz nazwe kolumny id
				prepstmt.execute();
			}

			PreparedStatement prepstmt=conn.prepareStatement("DELETE FROM klienci WHERE id_klienta="+id+";");//usun klienta z tabeli klienci
			prepstmt.execute();

			prepstmt=conn.prepareStatement("DROP TABLE znajomi"+id+";");//usun liste znajomych klienta
			prepstmt.execute();

			prepstmt=conn.prepareStatement("DROP TABLE wiadomosci"+id+";");//usun liste wiadomosci klienta
			prepstmt.execute();
		}catch(SQLException e) {
			System.err.println("Blad przy usuwaniu");
			return false;
		}
		return true;
	}

	public boolean zmienDaneKlienta(String cozmienic, String nowedane, int id) {//zmiana np nazwiska lub hasla
		try {
			PreparedStatement prepstmt=conn.prepareStatement("UPDATE klienci SET "+cozmienic+"='"+nowedane+"' WHERE id_klienta="+id+";");
			prepstmt.execute();
		}catch(SQLException e) {
			System.err.println("Blad przy zmianie danych");
			return false;
		}
		return true;
	}

	public Klient getDaneKlienta(int id){//pobranie imienia,nazwiska i email okreslonego klienta
		Klient dane=new Klient();
		try {
			PreparedStatement prepstmt=conn.prepareStatement("SELECT * FROM klienci WHERE id_klienta="+id+";");
			ResultSet result=prepstmt.executeQuery();
			if(result.next()) {
				dane.setImie(result.getString("imie"));
				dane.setNazwisko(result.getString("nazwisko"));
				dane.setEmail(result.getString("email"));
				dane.setDom(result.getString("dom"));
			}
		} catch (SQLException e) {
			dane.setImie("Blad");
			dane.setNazwisko("pobrania");
			dane.setEmail("danych");
			dane.setDom("klienta");
			e.printStackTrace();
		}
		return dane;
	}

	public int getIdKlienta(String login) {//pobranie id klienta o podanym loginie
		int id_klienta=0;
		try {
			ResultSet result=stat.executeQuery("SELECT id_klienta FROM klienci WHERE email='"+login+"';");
			if(result.next())id_klienta=result.getInt("id_klienta");
		} catch (SQLException e) {
			System.out.println(e);
		}

		return id_klienta;
	}

	public boolean checkIfLoginExist(String login) {//pobranie id klienta o podanym loginie
		int exist;
		try {
			ResultSet result=stat.executeQuery("SELECT id_klienta FROM klienci WHERE email='"+login+"';");
			if(result.next())exist=result.getInt("id_klienta");
			else return false;
		} catch (SQLException e) {
			exist=0;
		}

		if(exist!=0)return true;
		else return false;
	}

	public boolean logowanie(String email, String haslo) {//funkcja sprawdzajaca logowanie
		int zalogowany;
		try {
			ResultSet result=stat.executeQuery("SELECT id_klienta FROM klienci WHERE email='"+email+"' AND haslo='"+haslo+"';");
			if(result.next()) {
				int x=result.getInt("id_klienta");
				zalogowany=x;
			}
			else return false;
		} catch (SQLException e) {
			zalogowany=0;
		}

		if(zalogowany!=0)return true;
		else return false;
	}

	public boolean dodajZnajomego(int idklienta, int idznajomego) {//dodanie nowego znajomego
				try{
				Timestamp date = new Timestamp(new Date().getTime());//pobranie aktualnej daty
				PreparedStatement prepstmt=conn.prepareStatement("INSERT INTO znajomi"+idklienta+" VALUES (?,?);");
				prepstmt.setInt(1, idznajomego);
				prepstmt.setTimestamp(2, date);
				prepstmt.execute();//dodaj znajomego u klienta

				prepstmt=conn.prepareStatement("INSERT INTO znajomi"+idznajomego+" VALUES (?,?);");
				prepstmt.setInt(1, idklienta);
				prepstmt.setTimestamp(2, date);
				prepstmt.execute();//dodaj klienta u znajomego
				return  true;

		}catch(SQLException e) {
			e.printStackTrace();
			System.err.println("Blad przy dodawaniu znajomego");
			return false;
		}
	}

	public boolean usunZnajomego(int idklienta, int idznajomego) {//usuniecie znajomego
		try {

				PreparedStatement prepstmt=conn.prepareStatement("DELETE FROM znajomi"+idklienta+" WHERE id_znajomego=?;");
				prepstmt.setInt(1, idznajomego);
				prepstmt.execute();//usun znajomego u klienta

				prepstmt=conn.prepareStatement("DELETE FROM znajomi"+idznajomego+" WHERE id_znajomego=?;");
				prepstmt.setInt(1, idklienta);
				prepstmt.execute();//usun klienta u znajomego
				return true;

		}catch(SQLException e) {
			e.printStackTrace();
			System.err.println("Blad przy usuwaniu znajomego");
			return false;
		}

	}

	public List<Integer> getIDZnajomych(int id) {//pobranie listy wszystkich znajomych klienta
		List<Integer> dane=new ArrayList<Integer>();
		try {
			ResultSet result=stat.executeQuery("SELECT id_znajomego FROM znajomi"+id+";");
			while(result.next()) {
				dane.add(result.getInt("id_znajomego"));
			}
		} catch (SQLException e) {
			System.err.println("Nie sa znajomymi");
		}
		return dane;
	}

	public boolean dodajWiadomosc(int idklienta, Wiadomosc wiad) {
		try {
			PreparedStatement prepstmt=conn.prepareStatement("INSERT INTO wiadomosci"+idklienta+" VALUES (NULL,?,?,?,?);");
			prepstmt.setString(1, wiad.getTresc());
			prepstmt.setInt(2, wiad.getId_znajomego());
			Timestamp data=new Timestamp(wiad.getData_wyslania().getTime());
			prepstmt.setTimestamp(3, data);
			prepstmt.setInt(4, wiad.getCzywyslana());
			prepstmt.execute();//dodaj znajomego u klienta
		}catch(SQLException e) {
			e.printStackTrace();
			System.err.println("Blad przy zapisie wiadomosci");
			return false;
		}
		return true;
	}

	public List<Wiadomosc> getWiadomosci(int id_klienta,int id_znajomego) {//usuniecie wiadomosci o wskazanym id
		List<Wiadomosc> wyniki=new ArrayList<Wiadomosc>();//lista do przechowania wiadomosci
		if(id_znajomego==0) {//wybierz wszystkie wiadomosci
			try {
				ResultSet result=stat.executeQuery("SELECT * FROM wiadomosci"+id_klienta+";");
				while(result.next()) {
					wyniki.add(new Wiadomosc(result.getString("tresc"), result.getInt("id_znajomego"),
							result.getDate("data_wyslania"),result.getInt("czy_wyslana")));
				}
			}catch(SQLException e) {
				e.printStackTrace();
				System.err.println("Blad przy pobraniu wiadomosci");
			}
		}
		else {//usuwa wiadomosci znajomego
			try {
				ResultSet result=stat.executeQuery("SELECT * FROM wiadomosci"+id_klienta+" WHERE id_znajomego="+id_znajomego+";");
				while(result.next()) {
					wyniki.add(new Wiadomosc(result.getString("tresc"), result.getInt("id_znajomego"),
							result.getDate("data_wyslania"),result.getInt("czy_wyslana")));
				}
			}catch(SQLException e) {
				e.printStackTrace();
				System.err.println("Blad przy pobraniu wiadomosci");
			}
		}
		return wyniki;
	}

	public String getHasloKlienta(String imie,String nazwisko,String email){
		String haslo;
		try {
			ResultSet result=stat.executeQuery("SELECT haslo FROM klienci WHERE email='"+email+"'AND imie='"+imie+"'AND nazwisko='"+nazwisko+"';");
			haslo = result.getString("haslo");
		} catch (SQLException e) {
			e.printStackTrace();
			haslo = null;
		}
		return haslo;
	}

	public List<WiadomoscDomu> getWiadomosci(String dom) {//usuniecie wiadomosci o wskazanym id
		List<WiadomoscDomu> wyniki=new ArrayList<WiadomoscDomu>();//lista do przechowania wiadomosci

			try {
				ResultSet result=stat.executeQuery("SELECT * FROM " + dom + ";");
				while(result.next()) {
					wyniki.add(
							new WiadomoscDomu(
									result.getString("login"),
									result.getString("tresc")
							));
				}

			}catch(SQLException e) {
				e.printStackTrace();
				System.err.println("Blad przy pobraniu wiadomosci");
			}

		return wyniki;
	}

	public boolean wyslijWiadomoscDomu(String login2, String dom, String wiadomosc){
		try {

			PreparedStatement prepstmt=conn.prepareStatement("INSERT INTO "+dom+" VALUES (NULL,?,?);");
			prepstmt.setString(1, login2);
			prepstmt.setString(2, wiadomosc);
			prepstmt.execute();//dodaj znajomego u klienta

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void closeConnection() {
		try {
			conn.close();
		}catch(SQLException e) {
			System.err.println("Problem z zamknieciem polaczenia");
			e.printStackTrace();
		}
	}

	// nie uzywane
	public boolean usunWiadomosc(int id_klienta,int id_znajomego) {//usuniecie wiadomosci o wskazanym id
		if(id_znajomego==0) {//wyczysc wszystkie wiadomosci
			try {
				PreparedStatement prepstmt=conn.prepareStatement("DELETE FROM wiadomosci"+id_klienta+";");
				prepstmt.execute();
			}catch(SQLException e) {
				e.printStackTrace();
				System.err.println("Blad przy zapisie wiadomosci");
				return false;
			}
			return true;
		}
		else {//usuwa wiadomosci znajomego
			try {
				PreparedStatement prepstmt=conn.prepareStatement("DELETE FROM wiadomosci"+id_klienta+" WHERE id_znajomego="+id_znajomego+";");
				prepstmt.execute();
			}catch(SQLException e) {
				e.printStackTrace();
				System.err.println("Blad przy zapisie wiadomosci");
				return false;
			}
			return true;

		}
	}

	public List<Klient> getZnajomi(int id) {//pobranie listy wszystkich znajomych klienta
		List<Klient> dane=new ArrayList<Klient>();
		try {
			ResultSet result=stat.executeQuery("SELECT id_znajomego FROM znajomi"+id+";");
			List<Integer>listaznajomych=new ArrayList<Integer>();
			while(result.next()) {
				int idznajomego=result.getInt("id_znajomego");
				listaznajomych.add(idznajomego);
			}
			for(int i=0;i<listaznajomych.size();i++) {
				Klient znajomy=getDaneKlienta(listaznajomych.get(i));
				String imie=znajomy.getImie();
				String nazwisko=znajomy.getNazwisko();
				String email=znajomy.getEmail();
				String dom=znajomy.getDom();
				dane.add(new Klient(imie,nazwisko,email,dom));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dane;
	}
}
