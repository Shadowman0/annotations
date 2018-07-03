package de.etengo.eemweb.firma.integrationtest.ansprechpartner;

import static de.etengo.eemweb.commons.core.serialization.ObjectMapping.describeAsJsonSafely;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatDto;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatText;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatTimestamp;
import static de.etengo.eemweb.commons.testextension.mocking.HoverflyResponses.created;
import static de.etengo.eemweb.firma.api.AnsprechpartnerAnlegenDTOBuilder.aAnsprechpartnerAnlegenDTO;
import static de.etengo.eemweb.firma.api.AnsprechpartnerDTOBuilder.aAnsprechpartnerDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import de.etengo.eemweb.commons.core.api.UuidDto;
import de.etengo.eemweb.commons.core.serialization.ObjectMapping;
import de.etengo.eemweb.commons.db.entity.errorhandling.ResourceNotFoundException;
import de.etengo.eemweb.commons.db.entity.errorhandling.ResourceToUpdateNotValidException;
import de.etengo.eemweb.commons.test.util.UuidConstants;
import de.etengo.eemweb.commons.testextension.integration.AbstractIntegrationTest;
import de.etengo.eemweb.commons.testextension.integration.Role;
import de.etengo.eemweb.commons.testextension.mocking.HoverflyStarterRule;
import de.etengo.eemweb.firma.ansprechpartner.Ansprechpartner;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerController;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerRepository;
import de.etengo.eemweb.firma.api.AnsprechpartnerAnlegenNestedDTO;
import de.etengo.eemweb.firma.api.AnsprechpartnerAnlegenNestedDTOBuilder;
import de.etengo.eemweb.firma.api.AnsprechpartnerDTO;
import de.etengo.eemweb.firma.api.AnsprechpartnerDTOBuilder;
import de.etengo.eemweb.firma.api.PersonIdDTO;
import de.etengo.eemweb.firma.integrationtest.FirmaHoverflyConfig;
import de.etengo.eemweb.firma.integrationtest.ServiceResponses;

public class AnsprechpartnerControllerIntegrationTest extends AbstractIntegrationTest {
	
	private static final UUID STANDORT_ID = UuidConstants.ID_1;
	private static final UUID ANSPRECHPARTNER_ID = UuidConstants.ID_1;
	private static final UUID NON_EXISTING_ID = UuidConstants.generateUUID(999L);
	private static final UUID PERSON_ID = UuidConstants.ID_10;
	
	@Autowired
	private AnsprechpartnerController controller;
	
	@Autowired
	private AnsprechpartnerRepository repository;
	
	@Rule
	public HoverflyStarterRule hoverfly = HoverflyStarterRule.startHoverfly(FirmaHoverflyConfig.config());
	
	@Before
	public void setUp() {
		hoverfly.addMatching(ServiceResponses.setupPersonResponse(created(new PersonIdDTO(new UuidDto(PERSON_ID)))));
	}
	
	@Test
	public void getAnsprechpartner_ShouldReturnAnsprechpartner() {
		AnsprechpartnerDTO ansprechpartner = controller.get(ANSPRECHPARTNER_ID);
		assertThatText(ansprechpartner).isEqualToTrimmed(getClass().getResource("ansprechpartner.txt"));
	}
	
	@Test
	public void createAnsprechpartner_ShouldPersistAnsprechpartnerWithId() {
		authenticateWithAllRoles();
		
		PersonIdDTO result = controller.create(describeAsJsonSafely(ansprechpartnerNestedDTO()));
		UUID personId = result.getPerson().getId();
		
		Ansprechpartner retrieved = repository.findByPersonId(personId).get();
		assertThatTimestamp(retrieved.getLastModified()).isCloseToNow();
	}
	
	@Test
	public void createAnsprechpartner_InvalidStandortId_ShouldNotPersistAnsprechpartnerWithId() {
		authenticateWithAllRoles();
		
		assertThatThrownBy(() -> controller.create(describeAsJsonSafely(ansprechpartnerNestedDTOWithStandortId(UUID.randomUUID()))))
				.isInstanceOf(ResourceNotFoundException.class);
	}
	
	@Test
	public void updateAnsprechpartner_ShouldUpdateExistingAnsprechpartner() {
		authenticateWithAllRoles();
		AnsprechpartnerDTO ansprechpartner = controller.get(ANSPRECHPARTNER_ID);
		AnsprechpartnerDTO modifiedAnsprechpartner = aAnsprechpartnerDTO().copy(ansprechpartner).but().withAbteilung("andere Abteilung").build();
		AnsprechpartnerDTO updatedAnsprechpartner = controller.update(modifiedAnsprechpartner, ANSPRECHPARTNER_ID);
		AnsprechpartnerDTO retrievedAnsprechpartner = controller.get(ANSPRECHPARTNER_ID);
		
		assertThatDto(retrievedAnsprechpartner).isEqualToComparingDomainFields(updatedAnsprechpartner);
		assertThat(retrievedAnsprechpartner.getAbteilung()).isEqualTo("andere Abteilung");
		assertThatTimestamp(retrievedAnsprechpartner.getLastModified()).isCloseToNow();
	}
	
	@Test
	public void updateAnsprechpartner_WithNonExistingId_ShouldThrowException() {
		authenticateWithAllRoles();
		AnsprechpartnerDTO ansprechpartnerToSave = ansprechpartnerDTOBuilder().build();
		assertThatThrownBy(() -> controller.update(ansprechpartnerToSave, NON_EXISTING_ID)).isInstanceOf(ResourceToUpdateNotValidException.class);
	}
	
	@Test
	public void updateAnsprechpartner_WithOlderLastModified_ShouldThrowException() {
		authenticateWithAllRoles();
		
		UUID personId = controller.create(ObjectMapping.describeAsJsonSafely(ansprechpartnerNestedDTO())).getPerson().getId();
		UUID ansprechpartnerId = repository.findByPersonId(personId).get().getId();
		AnsprechpartnerDTO ansprechpartnerToSave = ansprechpartnerDTOBuilder().withLastModified(LocalDateTime.now().minusHours(42)).build();
		
		assertThatThrownBy(() -> controller.update(ansprechpartnerToSave, ansprechpartnerId))
				.isInstanceOf(ObjectOptimisticLockingFailureException.class);
	}
	
	@Test
	public void updateAnsprechpartner_WithLastModifiedNull_ShouldThrowException() {
		authenticateWithAllRoles();
		
		UUID personId = controller.create(ObjectMapping.describeAsJsonSafely(ansprechpartnerNestedDTO())).getPerson().getId();
		UUID ansprechpartnerId = repository.findByPersonId(personId).get().getId();
		
		AnsprechpartnerDTO ansprechpartnerToUpdate = ansprechpartnerDTOBuilder().withLastModified(null).build();
		
		assertThatThrownBy(() -> controller.update(ansprechpartnerToUpdate, ansprechpartnerId)).isInstanceOf(ResourceToUpdateNotValidException.class);
	}
	
	@Test
	public void securedEndpoints() {
		authenticateWithAllRolesBut(Role.FIRMA_WRITE)
				.expectAccessDenied(() -> controller.create(ObjectMapping.describeAsJsonSafely(ansprechpartnerDTOBuilder().build())));
		authenticateWithAllRolesBut(Role.FIRMA_WRITE)
				.expectAccessDenied(() -> controller.update(ansprechpartnerDTOBuilder().build(), ANSPRECHPARTNER_ID));
	}
	
	private AnsprechpartnerAnlegenNestedDTO ansprechpartnerNestedDTO() {
		return ansprechpartnerNestedDTOWithStandortId(STANDORT_ID);
	}
	
	private AnsprechpartnerAnlegenNestedDTO ansprechpartnerNestedDTOWithStandortId(UUID standortId) {
		return AnsprechpartnerAnlegenNestedDTOBuilder.aAnsprechpartnerAnlegenNestedDTO()
				.withNewAnsprechpartner(aAnsprechpartnerAnlegenDTO().withStandortId(standortId).build()).build();
	}
	
	private AnsprechpartnerDTOBuilder ansprechpartnerDTOBuilder() {
		return AnsprechpartnerDTOBuilder.aAnsprechpartnerDTO().withStandortId(STANDORT_ID);
	}
	
}
