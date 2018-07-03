package de.etengo.eemweb.firma.integrationtest.ansprechpartner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.etengo.eemweb.commons.test.util.UuidConstants;
import de.etengo.eemweb.commons.testextension.integration.AbstractIntegrationTest;
import de.etengo.eemweb.firma.ansprechpartner.Ansprechpartner;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerRepository;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerService;

public class AnsprechpartnerServiceIntegrationTest extends AbstractIntegrationTest {
	
	private static final UUID ANSPRECHPARTNER_ID = UuidConstants.ID_1;
	
	@Autowired
	private AnsprechpartnerService service;
	
	@Autowired
	private AnsprechpartnerRepository repo;
	
	@Test
	public void updateKandidat_setsPersonIdFromDatabase() {
		Ansprechpartner existing = repo.findById(ANSPRECHPARTNER_ID).get();
		UUID oldPersonId = existing.getPersonId();
		flushAndClear();
		
		existing.setPersonId(null);
		Ansprechpartner saved = service.update(existing);
		
		assertThat(oldPersonId).isNotNull();
		assertThat(saved.getPersonId()).isEqualTo(oldPersonId);
	}
	
}
