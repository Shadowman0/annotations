package de.etengo.eemweb.firma.ansprechpartner;

import static de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerBuilder.aAnsprechpartner;
import static de.etengo.eemweb.firma.api.AnsprechpartnerDTOBuilder.aAnsprechpartnerDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.etengo.eemweb.commons.core.api.UuidDto;
import de.etengo.eemweb.commons.db.entity.errorhandling.ResourceNotFoundException;
import de.etengo.eemweb.commons.security.authentication.AuthenticationInfoService;
import de.etengo.eemweb.commons.test.util.UuidConstants;
import de.etengo.eemweb.commons.validation.EmailValidator;
import de.etengo.eemweb.commons.validation.EtengoValidationWithViolationsException;
import de.etengo.eemweb.commons.validation.errorhandling.EtengoValidator;
import de.etengo.eemweb.firma.api.AnsprechpartnerAnlegenDTO;
import de.etengo.eemweb.firma.api.AnsprechpartnerDTO;
import de.etengo.eemweb.firma.api.PersonIdDTO;

@RunWith(MockitoJUnitRunner.class)
public class AnsprechpartnerControllerTest {
	
	private static final String AUTH_TOKEN = "auth token";
	
	private static final UUID ID = UUID.fromString("663b42d7-3155-4687-a2f5-7a934eb5752c");
	
	@Mock
	private AnsprechpartnerService service;
	
	@Mock
	private AnsprechpartnerMapper mapper;
	
	@Mock
	private AnsprechpartnerAnlegenMapper anlegenMapper;
	
	@Mock
	private StandortExisting standortExisting;
	
	@Mock
	private PersonSecuredInternalClient personClient;
	
	@Mock
	private AuthenticationInfoService authenticationInfoService;

	@Spy
	private EtengoValidator dtoValidator = new EtengoValidator();
	
	@Captor
	private ArgumentCaptor<UUID> personIdArgument;
	@Captor
	private ArgumentCaptor<AnsprechpartnerAnlegenDTO> ansprechpartnerAnlegenArgument;
	@Captor
	private ArgumentCaptor<byte[]> personClientArgument;
	
	@InjectMocks
	private AnsprechpartnerController controller;
	
	private AnsprechpartnerDTO dtoInput;
	private Ansprechpartner entity;
	private Ansprechpartner savedEntity;
	private AnsprechpartnerDTO dtoResult;
	
	@Before
	public void setUp() {
		dtoInput = aAnsprechpartnerDTO().build();
		entity = aAnsprechpartner().build();
		savedEntity = aAnsprechpartner().build();
		dtoResult = aAnsprechpartnerDTO().build();
		
		when(mapper.asDto(same(savedEntity))).thenReturn(dtoResult);
	}
	
	@Test
	public void get() {
		when(service.get(ID)).thenReturn(savedEntity);
		AnsprechpartnerDTO result = controller.get(ID);
		assertThat(result).isSameAs(dtoResult);
	}
	
	@Test
	public void create() {
		UUID standortId = UUID.fromString("aee6b13a-17af-467e-a13a-51e2793e346e");
		UUID personId = UuidConstants.ID_123;
		String ansprechpartnerJson = "{\"person\":{\"vorname\":\"herbert\",\"nachname\":\"müller\"},\"ansprechpartner\":{\"standortId\":\""
				+ standortId + "\",\"emailadresse\":\"e@mail.de\"}}";
		Ansprechpartner newAnsprechpartner = aAnsprechpartner().withPersonId(personId).withStandortId(standortId).withEmailadresse("e-mail").build();
		PersonIdDTO response = new PersonIdDTO(new UuidDto(personId));
		
		when(authenticationInfoService.getCompleteAuthHeader()).thenReturn(AUTH_TOKEN);
		when(personClient.createPerson(eq(AUTH_TOKEN), any())).thenReturn(response);
		when(anlegenMapper.fromNewAnsprechpartnerAnlegenDtoWithPersonId(eq(personId), any())).thenReturn(newAnsprechpartner);
		
		PersonIdDTO result = controller.create(ansprechpartnerJson);
		
		verify(anlegenMapper).fromNewAnsprechpartnerAnlegenDtoWithPersonId(personIdArgument.capture(), ansprechpartnerAnlegenArgument.capture());
		verify(personClient).createPerson(eq(AUTH_TOKEN), personClientArgument.capture());
		
		assertThat(result).isSameAs(response);
		assertThat(personIdArgument.getValue()).isEqualTo(personId);
		assertThat(ansprechpartnerAnlegenArgument.getValue().toString()).isEqualTo(
				"AnsprechpartnerAnlegenDTO[standortId=aee6b13a-17af-467e-a13a-51e2793e346e,telefonnummer=,emailadresse=e@mail.de,position=,abteilung=,vorgesetzterId=Optional.empty]");
		assertThat(new String(personClientArgument.getValue())).contains(
				"\"ansprechpartner\":{\"standortId\":\"aee6b13a-17af-467e-a13a-51e2793e346e\",\"emailadresse\":\"e@mail.de\"},\"rollen\":[{\"rolle\":11}]}");
		verify(service).create(newAnsprechpartner);
		
	}
	
	@Test
	public void createAnsprechpartner_AllFieldsInvalid_ReportsViolations() {
		
		String ansprechpartnerJson = String.format("{\"ansprechpartner\":{"//
				+ "\"standortId\":null,"//
				+ "\"emailadresse\":\"%s\","//
				+ "\"telefonnummer\":\"%s\","//
				+ "\"position\":\"%s\","//
				+ "\"abteilung\":\"%s\"}}", //
				StringUtils.repeat("X", 101), StringUtils.repeat("X", 41), StringUtils.repeat("X", 51), StringUtils.repeat("X", 51));
		
		assertThatThrownBy(() -> controller.create(ansprechpartnerJson)).isInstanceOfSatisfying(EtengoValidationWithViolationsException.class,
				exception -> assertThat(exception.getViolations()).containsExactlyInAnyOrder(//
						"ansprechpartner.standortId darf nicht null sein", //
						"ansprechpartner.telefonnummer muss zwischen 0 und 40 liegen", //
						"ansprechpartner.emailadresse muss zwischen 0 und 100 liegen", //
						"ansprechpartner.position muss zwischen 0 und 50 liegen", //
						"ansprechpartner.abteilung muss zwischen 0 und 50 liegen",
						"ansprechpartner.emailadresse muss auf Ausdruck \"" + EmailValidator.OPTIONAL_EMAIL_REGEXP + "\" passen"));
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void createAnsprechpartner_StandortServiceThrowsError_PersonClientNotInvoked_AnsprechpartnerNotSaved() {
		UUID standortId = UUID.randomUUID();
		String ansprechpartnerJson = "{\"ansprechpartner\":{\"standortId\":\"" + standortId + "\",\"emailadresse\":\"e@mail.de\"}}";
		
		doThrow(new ResourceNotFoundException("Standort nicht gefunden")).when(standortExisting).exists(standortId);
		
		controller.create(ansprechpartnerJson);
		
		verify(personClient, never()).createPerson(any(), any());
		verify(service, never()).create(any());
	}
	
	@Test
	public void update() {
		when(mapper.fromUpdatedDTO(same(dtoInput), eq(ID))).thenReturn(entity);
		when(service.update(same(entity))).thenReturn(savedEntity);
		
		AnsprechpartnerDTO result = controller.update(dtoInput, ID);
		
		assertThat(result).isSameAs(dtoResult);
	}
	
}
