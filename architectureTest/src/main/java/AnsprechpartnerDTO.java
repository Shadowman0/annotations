package de.etengo.eemweb.firma.api;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonRootName;

import de.etengo.eemweb.commons.core.api.VersionedDTO;
import de.etengo.eemweb.commons.core.builder.GenerateBuilder;
import de.etengo.eemweb.commons.validation.EmailValidator;

@JsonRootName("ansprechpartner")
public class AnsprechpartnerDTO extends VersionedDTO {
	
	private final UUID id;
	
	@NotNull
	private final UUID personId;
	
	@NotNull
	private final UUID standortId;
	
	@Size(max = 40)
	private final String telefonnummer;
	
	@Size(max = 100)
	@Pattern(regexp = EmailValidator.OPTIONAL_EMAIL_REGEXP)
	private final String emailadresse;
	
	@Size(max = 50)
	private final String position;
	
	@Size(max = 50)
	private final String abteilung;
	
	private final Optional<UUID> vorgesetzterId;
	
	public AnsprechpartnerDTO(UUID id, UUID personId, LocalDateTime lastModified, UUID standortId, String telefonnummer, String emailadresse,
			String position, String abteilung, Optional<UUID> vorgesetzterId) {
		super(lastModified);
		this.id = id;
		this.personId = personId;
		this.standortId = standortId;
		this.telefonnummer = telefonnummer;
		this.emailadresse = emailadresse;
		this.position = position;
		this.abteilung = abteilung;
		this.vorgesetzterId = vorgesetzterId;
	}
	
	@GenerateBuilder
	AnsprechpartnerDTO(UUID id, UUID personId, LocalDateTime lastModified, UUID standortId, String telefonnummer, String emailadresse,
			String position, String abteilung, UUID vorgesetzterId) {
		this(id, personId, lastModified, standortId, telefonnummer, emailadresse, position, abteilung, Optional.ofNullable(vorgesetzterId));
	}
	
	AnsprechpartnerDTO() {
		this(null, null, null, null, "", "", "", "", Optional.empty());
	}
	
	public UUID getId() {
		return id;
	}
	
	public UUID getStandortId() {
		return standortId;
	}
	
	public String getTelefonnummer() {
		return telefonnummer;
	}
	
	public String getEmailadresse() {
		return emailadresse;
	}
	
	public String getPosition() {
		return position;
	}
	
	public String getAbteilung() {
		return abteilung;
	}
	
	public Optional<UUID> getVorgesetzterId() {
		return vorgesetzterId;
	}
	
	public UUID getPersonId() {
		return personId;
	}
	
}
