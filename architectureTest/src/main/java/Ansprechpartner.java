package de.etengo.eemweb.firma.ansprechpartner;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.Entity;

import de.etengo.eemweb.commons.core.builder.GenerateBuilder;
import de.etengo.eemweb.commons.db.entity.VersionedEntityWithUuid;

@Entity
@GenerateBuilder
public class Ansprechpartner extends VersionedEntityWithUuid {
	
	private UUID personId;
	
	private UUID standortId;
	private String telefonnummer;
	private String emailadresse;
	private String position;
	private String abteilung;
	private UUID vorgesetzterId;
	
	public UUID getStandortId() {
		return standortId;
	}
	
	public void setStandortId(UUID standortId) {
		this.standortId = standortId;
	}
	
	public String getTelefonnummer() {
		return telefonnummer;
	}
	
	public void setTelefonnummer(String telefonnummer) {
		this.telefonnummer = telefonnummer;
	}
	
	public String getEmailadresse() {
		return emailadresse;
	}
	
	public void setEmailadresse(String emailadresse) {
		this.emailadresse = emailadresse;
	}
	
	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getAbteilung() {
		return abteilung;
	}
	
	public void setAbteilung(String abteilung) {
		this.abteilung = abteilung;
	}
	
	public UUID getPersonId() {
		return personId;
	}
	
	public void setPersonId(UUID personId) {
		this.personId = personId;
	}
	
	public Optional<UUID> getVorgesetzterId() {
		return Optional.ofNullable(vorgesetzterId);
	}
	
	public void setVorgesetzterId(UUID vorgesetzterID) {
		this.vorgesetzterId = vorgesetzterID;
	}
	
	public void setVorgesetzterId(Optional<UUID> vorgesetzterID) {
		this.vorgesetzterId = vorgesetzterID.orElse(null);
	}
	
}
