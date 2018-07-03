package de.etengo.eemweb.firma.api;

import java.time.LocalDateTime;
import java.util.UUID;

import de.etengo.eemweb.commons.test.util.UuidConstants;

public class AnsprechpartnerDTOFactory {
	
	private static final UUID PERSON_ID = UuidConstants.ID_19;
	private static final UUID VORGESETZTER_ID = UuidConstants.ID_1;
	private static final UUID ID = UUID.fromString("8b7a4801-f4e1-407a-8045-ce8f1e322f54");
	private static final UUID STANDORT_ID = UUID.fromString("aee6b13a-17af-467e-a13a-51e2793e346e");
	private static final String TELEFONNUMMER = "123456";
	private static final String EMAILADRESSE = "nicht.da@gibts.nicht.com";
	private static final String ABTEILUNG = "C45a";
	private static final String POSTION = "Vorstand";
	
	public static AnsprechpartnerDTOBuilder aCompleteAnsprechpartnerDTOBuilder() {
		return AnsprechpartnerDTOBuilder.aAnsprechpartnerDTO().withId(ID).withStandortId(STANDORT_ID).withTelefonnummer(TELEFONNUMMER)
				.withEmailadresse(EMAILADRESSE).withPosition(POSTION).withAbteilung(ABTEILUNG).withLastModified(LocalDateTime.of(2001, 2, 3, 4, 5, 6))
				.withVorgesetzterId(VORGESETZTER_ID).withPersonId(PERSON_ID);
	}
	
	public static AnsprechpartnerDTOBuilder aMinimalAnsprechpartnerDTOBuilder() {
		return AnsprechpartnerDTOBuilder.aAnsprechpartnerDTO().withStandortId(STANDORT_ID).withPersonId(PERSON_ID);
	}
}
