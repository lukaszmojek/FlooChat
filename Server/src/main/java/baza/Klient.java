package baza;

public class Klient {
	String imie,nazwisko,email,dom;
	public String getImie() {
		return imie;
	}
	public void setImie(String imie) {
		this.imie = imie;
	}
	public String getNazwisko() {
		return nazwisko;
	}
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDom() {
		return dom;
	}
	public void setDom(String dom) {
		this.dom = dom;
	}
	public Klient() {
		
	}
	public Klient(String imie, String nazwisko, String email, String dom) {
		super();
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.email = email;
		this.dom = dom;
	}
	@Override
	public String toString() {
		return "Klient [imie=" + imie + ", nazwisko=" + nazwisko + ", email=" + email + ", dom=" + dom + "]";
	}
}
