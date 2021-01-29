package baza;

import java.util.Date;

public class Wiadomosc {
	String tresc;
	int id_znajomego;
	Date data_wyslania;
	int czywyslana;
	public String getTresc() {
		return tresc;
	}
	public void setTresc(String tresc) {
		this.tresc = tresc;
	}
	public int getId_znajomego() {
		return id_znajomego;
	}
	public void setId_znajomego(int id_znajomego) {
		this.id_znajomego = id_znajomego;
	}
	public Date getData_wyslania() {
		return data_wyslania;
	}
	public int getCzywyslana() {
		return czywyslana;
	}
	public void setCzywyslana(int czywyslana) {
		this.czywyslana = czywyslana;
	}
	public Wiadomosc(String tresc, int id_znajomego, Date data_wyslania,int czywyslana) {
		super();
		this.tresc = tresc;
		this.id_znajomego = id_znajomego;
		this.data_wyslania = data_wyslania;
		this.czywyslana=czywyslana;
	}

	@Override
	public String toString() {
		return czywyslana+tresc + "\n";
	}
	
}
