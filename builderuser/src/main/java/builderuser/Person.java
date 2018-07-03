package builderuser;

import example.domain.MyAnnotation;

@MyAnnotation(target = PersonNeu.class)
// @GeneratePojoBuilder
public class Person {
	private String vorname;
	private String nachname;
	private String strasse;
	private String hausnummer;
	private String lieblingsfarbe;
	private int alter;

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getLieblingsfarbe() {
		return lieblingsfarbe;
	}

	public void setLieblingsfarbe(String lieblingsfarbe) {
		this.lieblingsfarbe = lieblingsfarbe;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String name) {
		this.vorname = name;
	}

	public int getAlter() {
		return alter;
	}

	public void setAlter(int alter) {
		this.alter = alter;
	}
}
