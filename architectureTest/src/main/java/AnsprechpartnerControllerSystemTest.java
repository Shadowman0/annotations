package de.etengo.eemweb.firma.integrationtest.ansprechpartner;

import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatEntity;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatJson;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatResponse;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatText;
import static de.etengo.eemweb.commons.test.ResourceUtil.readAndTrimAndJoin;
import static de.etengo.eemweb.commons.testextension.mocking.HoverflyResponses.badRequest;
import static de.etengo.eemweb.commons.testextension.mocking.HoverflyResponses.created;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Commit;

import de.etengo.eemweb.commons.core.api.ApiErrorDTO;
import de.etengo.eemweb.commons.core.api.UuidDto;
import de.etengo.eemweb.commons.core.serialization.ObjectMapping;
import de.etengo.eemweb.commons.test.ResourceUtil;
import de.etengo.eemweb.commons.test.json.JsonUtil;
import de.etengo.eemweb.commons.test.util.UuidConstants;
import de.etengo.eemweb.commons.testextension.integration.AbstractSecureSystemTest;
import de.etengo.eemweb.commons.testextension.integration.CommitImmediately;
import de.etengo.eemweb.commons.testextension.integration.Users;
import de.etengo.eemweb.commons.testextension.mocking.HoverflyStarterRule;
import de.etengo.eemweb.commons.validation.EmailValidator;
import de.etengo.eemweb.firma.ansprechpartner.Ansprechpartner;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerRepository;
import de.etengo.eemweb.firma.api.PersonIdDTO;
import de.etengo.eemweb.firma.integrationtest.FirmaHoverflyConfig;
import de.etengo.eemweb.firma.integrationtest.ServiceResponses;

public class AnsprechpartnerControllerSystemTest extends AbstractSecureSystemTest {
	
	private static final String ID_PATH = "/" + UuidConstants.ID_2;
	private static final String INVALID_ID = "/" + UuidConstants.generateUUID(999L);
	
	private static final String ENDPOINT = "/firma/ansprechpartner";
	
	private static final UUID PERSON_ID = UuidConstants.ID_9;
	
	@Autowired
	private AnsprechpartnerRepository repository;
	@Autowired
	private AnsprechpartnerRepositoryForTest repositoryForTest;
	
	@Rule
	public HoverflyStarterRule hoverfly = HoverflyStarterRule.startHoverfly(FirmaHoverflyConfig.config());
	
	public AnsprechpartnerControllerSystemTest() {
		super(ENDPOINT);
	}
	
	@Before
	public void setUp() {
		hoverfly.addMatching(ServiceResponses.setupPersonResponse(created(new PersonIdDTO(new UuidDto(PERSON_ID)))));
	}
	
	@Test
	public void getAnsprechpartner_Json() {
		String ansprechpartnerJson = get(ID_PATH).getBody();
		assertThatText(ansprechpartnerJson).isEqualToTrimmed(getClass().getResource("ansprechpartner.json"));
	}
	
	@Test
	public void getNonExistingAnsprechpartner_Throws404() {
		ResponseEntity<String> response = get(INVALID_ID.toString());
		assertThatResponse(response).hasStatusCode(404).jsonBody()
				.containsContentOfInAnyOrder(getClass().getResource("responseAnsprechpartner404.json"));
	}
	
	@Test
	@CommitImmediately
	public void postAnsprechpartner_IdIsZero_AnsprechpartnerIsSavedAndNewIdIsReturned() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = readAndTrimAndJoin(getClass().getResource("postAnsprechpartner.json"));
		
		ResponseEntity<String> response = post(ansprechpartnerToSave);
		
		Ansprechpartner retrievedAnsprechpartner = repository.findByPersonId(PERSON_ID).get();
		
		repository.delete(retrievedAnsprechpartner.getId());
		assertThatResponse(response).hasStatus(HttpStatus.CREATED);
		assertThatResponse(response).hasBody("{\"person\":{\"id\":\"00000000-0000-0000-0000-000000000009\"}}");
		assertThatEntity(retrievedAnsprechpartner).hasRecentLastModified();
		
	}
	
	@Test
	@CommitImmediately
	public void postAnsprechpartner_PersonServiceReturnsError_NoAnsprechpartnerIsCreated_AndErrorIsReturned_UsesHttpStatusFromResponse_NotFromContent() {
		login(Users.ADMIN);
		long oldAnsprechpartnerCount = repositoryForTest.count();
		hoverfly.clearAllMatchings();
		hoverfly.addMatching(ServiceResponses.setupPersonResponse(badRequest(new ApiErrorDTO("uri=/secured/person", "Validierungsfehler",
				HttpStatus.NOT_ACCEPTABLE.value(), asList("personWithRollen.rollen.rolle darf nicht null sein")))));
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("postAnsprechpartner.json"));
		
		ResponseEntity<String> response = post(ansprechpartnerToSave);
		
		assertThatResponse(response).hasStatus(HttpStatus.NOT_ACCEPTABLE).jsonBody()
				.containsContentOfInAnyOrder(getClass().getResource("responsePersonServiceFailure.json"));
		
		assertThat(repositoryForTest.count()).isEqualTo(oldAnsprechpartnerCount);
	}
	
	@Test
	public void postAnsprechpartnerInvalidEmail() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("postAnsprechpartnerInvalidEmail.json"));
		
		ResponseEntity<String> response = post(ansprechpartnerToSave);
		
		assertThatResponse(response).hasStatus(HttpStatus.BAD_REQUEST).errorsContainExactlyInAnyOrder(
				"ansprechpartner.emailadresse muss auf Ausdruck \"" + EmailValidator.OPTIONAL_EMAIL_REGEXP + "\" passen");
	}
	
	@Test
	public void postAnsprechpartnerFieldsTooLong() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("postAnsprechpartnerFieldsTooLong.json"));
		
		ResponseEntity<String> response = post(ansprechpartnerToSave);
		
		assertThatResponse(response).hasStatus(HttpStatus.BAD_REQUEST).errorsContainExactlyInAnyOrder(
				"ansprechpartner.telefonnummer muss zwischen 0 und 40 liegen", "ansprechpartner.emailadresse muss zwischen 0 und 100 liegen",
				"ansprechpartner.position muss zwischen 0 und 50 liegen", "ansprechpartner.abteilung muss zwischen 0 und 50 liegen");
	}
	
	@Test
	public void postAnsprechpartner_InvalidStandortId_ReturnsError() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("postAnsprechpartnerInvalidFK.json"));
		
		ResponseEntity<String> response = post(ansprechpartnerToSave);
		
		assertThatResponse(response).hasStatus(HttpStatus.NOT_FOUND).hasMessage("Standort nicht gefunden");
	}
	
	@Test
	public void post_EmptyJson_ThrowsBadRequest_AndReportsViolatedConstraint() {
		login(Users.ADMIN);
		ResponseEntity<String> response = post("{}");
		
		assertThatResponse(response).hasStatus(HttpStatus.BAD_REQUEST).hasMessage("Validierungsfehler")
				.errorsContainExactlyInAnyOrder("ansprechpartner.standortId darf nicht null sein");
	}
	
	@Test
	@CommitImmediately
	public void putAnsprechpartner_NeuerVorgesetzter() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("postAnsprechpartner.json"));
		
		String saved = post(ansprechpartnerToSave).getBody();
		UUID personId = UUID.fromString(JsonUtil.jsonAttribute(saved, "person.id"));
		
		Ansprechpartner ansprechpartner = repository.findByPersonId(personId).get();
		entityManager.detach(ansprechpartner);
		ansprechpartner.setVorgesetzterId(UuidConstants.ID_3);
		
		ResponseEntity<String> updatedAnsprechpartner = put("/" + ansprechpartner.getId(), ObjectMapping.describeAsJsonSafely(ansprechpartner));
		
		String retrievedAnsprechpartner = get("/" + ansprechpartner.getId()).getBody();
		
		assertThatJson(retrievedAnsprechpartner).hasRecentLastModified().hasFieldWithValue("vorgesetzterId", UuidConstants.ID_3.toString())
				.isEqualTo(updatedAnsprechpartner.getBody());
		
		repository.delete(ansprechpartner.getId());
	}
	
	@Test
	@Commit
	public void putAnsprechpartner_OptimisticLockException() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("postAnsprechpartner.json"));
		
		String body = post(ansprechpartnerToSave).getBody();
		UUID personId = UUID.fromString(JsonUtil.jsonAttribute(body, "person.id"));
		
		Ansprechpartner fromDb = repository.findByPersonId(personId).get();
		String ansprechpartnerJson = ObjectMapping.describeAsJsonSafely(fromDb);
		String modified = JsonUtil.setField(ansprechpartnerJson, "lastModified", "2000-06-22T18:07:28.365");
		
		ResponseEntity<String> response = put("/" + fromDb.getId(), modified);
		
		repository.delete(fromDb.getId());
		assertThatResponse(response).hasStatus(HttpStatus.CONFLICT)
				.hasMessageContaining("Ansprechpartner: Der Datensatz mit ID " + fromDb.getId() + " wurde von einem anderen Nutzer verändert!");
	}
	
	@Test
	@Commit
	public void putAnsprechpartner_IdInvalid_NotFoundError() {
		login(Users.ADMIN);
		String ansprechpartnerToSave = ResourceUtil.readAndTrimAndJoin(getClass().getResource("putAnsprechpartner.json"));
		ResponseEntity<String> response = put(INVALID_ID.toString(), ansprechpartnerToSave);
		assertThatResponse(response).hasStatus(HttpStatus.BAD_REQUEST).hasMessageContaining("Die zu aktualisierende Ressource ist nicht vorhanden");
	}
	
	@Test
	public void put_EmptyJson_ThrowsBadRequest_AndReportsViolatedConstraints() {
		ResponseEntity<String> response = put(ID_PATH, "{}");
		assertThatResponse(response).hasStatus(HttpStatus.BAD_REQUEST).bodyContains("ansprechpartner.standortId darf nicht null sein");
	}
	
	@Test
	public void accessAsOutcast() {
		assertThatResponse(put(ID_PATH, readAndTrimAndJoin(getClass().getResource("putAnsprechpartner.json")))).hasStatus(HttpStatus.FORBIDDEN);
		assertThatResponse(post(readAndTrimAndJoin(getClass().getResource("putAnsprechpartner.json")))).hasStatus(HttpStatus.FORBIDDEN);
	}
	
}
