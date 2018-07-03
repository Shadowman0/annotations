package de.etengo.eemweb.firma.ansprechpartner;

import static de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerBuilder.aAnsprechpartner;
import static de.etengo.eemweb.firma.person.SharedPersonBuilder.aSharedPerson;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.etengo.eemweb.commons.db.entity.errorhandling.ResourceNotFoundException;
import de.etengo.eemweb.commons.db.entity.errorhandling.ResourceToUpdateNotValidException;
import de.etengo.eemweb.commons.test.util.UuidConstants;
import de.etengo.eemweb.firma.messaging.ansprechpartner.AnsprechpartnerMessage;
import de.etengo.eemweb.firma.person.PersonService;
import de.etengo.eemweb.firma.person.SharedPerson;

@RunWith(MockitoJUnitRunner.class)
public class AnsprechpartnerServiceTest {
	
	private static final UUID ID = UUID.fromString("767a9c54-3dc8-461c-8260-21f43ce072c5");
	
	private static final UUID PERSON_ID = UuidConstants.ID_333;
	
	@InjectMocks
	private AnsprechpartnerService service;
	@Mock
	private AnsprechpartnerRepository repository;
	@Mock
	private AnsprechpartnerMapper ansprechpartnerMapper;
	@Mock
	private PersonService personService;
	@Mock
	private AnsprechpartnerPublisher messaging;
	@Captor
	private ArgumentCaptor<AnsprechpartnerMessage> ansprechpartnerCaptor;
	
	private Ansprechpartner savedAnsprechpartner;
	
	private Ansprechpartner validAnsprechpartner;
	
	@Before
	public void setUp() {
		savedAnsprechpartner = aAnsprechpartner().build();
		validAnsprechpartner = validAnsprechpartnerForUpdate();
	}
	
	@Test
	public void get() {
		when(repository.findById(ID)).thenReturn(Optional.of(savedAnsprechpartner));
		Ansprechpartner result = service.get(ID);
		assertThat(result).isSameAs(savedAnsprechpartner);
	}
	
	@Test
	public void getAnsprechpartnerByPersonId() {
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aCompleteAnsprechpartner();
		when(repository.findByPersonId(PERSON_ID)).thenReturn(Optional.of(ansprechpartner));
		
		Ansprechpartner result = service.getByPersonId(PERSON_ID);
		
		assertThat(result).isSameAs(ansprechpartner);
	}
	
	@Test
	public void getAnsprechpartnerByPersonId_InvalidPersonId_ThrowsException() {
		when(repository.findByPersonId(UuidConstants.ID_1234)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> service.getByPersonId(UuidConstants.ID_1234)).isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Ansprechpartner nicht gefunden");
	}
	
	@Test
	public void get_ThrowsExceptionIfNoAnsprechpartnerIsFound() {
		when(repository.findById(ID)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> service.get(ID)).isInstanceOf(ResourceNotFoundException.class).hasMessage("Ansprechpartner nicht gefunden");
	}
	
	@Test
	public void getAnsprechpartnerWithPersonForAnsprechpartner() {
		when(personService.get(UuidConstants.ID_1)).thenReturn(Optional.of(aSharedPerson().withOriginalId(UuidConstants.ID_1).build()));
		when(personService.get(UuidConstants.ID_3)).thenReturn(Optional.of(aSharedPerson().withOriginalId(UuidConstants.ID_3).build()));
		
		List<AnsprechpartnerWithPerson> result = service
				.getAnsprechpartnerWithPersonForAnsprechpartner(asList(ansprechpartnerWithIdAndPersonId(ID, UuidConstants.ID_1),
						ansprechpartnerWithIdAndPersonId(UUID.fromString("8404a048-570f-4544-b781-745123c933ae"), UuidConstants.ID_2),
						ansprechpartnerWithIdAndPersonId(UUID.fromString("663b42d7-3155-4687-a2f5-7a934eb5752c"), UuidConstants.ID_3)));
		
		assertThat(result.stream().map(this::describe)).containsExactly("<ap:" + ID + ",p:00000000-0000-0000-0000-000000000001>",
				"<ap:8404a048-570f-4544-b781-745123c933ae,p:00000000-0000-0000-ffff-ffffffffffff>",
				"<ap:663b42d7-3155-4687-a2f5-7a934eb5752c,p:00000000-0000-0000-0000-000000000003>");
	}
	
	@Test
	public void create_CreateAnsprechpartner_InvokesMessaging() {
		when(repository.save(same(validAnsprechpartner))).thenReturn(savedAnsprechpartner);
		
		Ansprechpartner result = service.create(validAnsprechpartner);
		
		verify(messaging).publish(ansprechpartnerCaptor.capture());
		assertThat(result).isSameAs(savedAnsprechpartner);
		assertThat(ansprechpartnerCaptor.getValue().getPersonId()).isEqualTo(savedAnsprechpartner.getPersonId());
	}
	
	@Test
	public void update_IdExistsAndLastModifiedIsSet_ReturnsAnsprechpartner() {
		Ansprechpartner existing = aAnsprechpartner().withPersonId(PERSON_ID).build();
		when(repository.exists(ID)).thenReturn(true);
		when(repository.findById(ID)).thenReturn(Optional.of(existing));
		when(repository.save(same(validAnsprechpartner))).thenReturn(savedAnsprechpartner);
		
		Ansprechpartner result = service.update(validAnsprechpartner);
		
		verify(messaging).publish(ansprechpartnerCaptor.capture());
		assertThat(result).isSameAs(savedAnsprechpartner);
		assertThat(validAnsprechpartner.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(ansprechpartnerCaptor.getValue().getPersonId()).isEqualTo(savedAnsprechpartner.getPersonId());
	}
	
	@Test
	public void update_IdDoesNotExist_ThrowsException() {
		when(repository.exists(ID)).thenReturn(false);
		assertThatThrownBy(() -> service.update(validAnsprechpartner)).isInstanceOf(ResourceToUpdateNotValidException.class);
	}
	
	@Test
	public void update_LastModifiedNull_ThrowsException() {
		when(repository.exists(ID)).thenReturn(true);
		Ansprechpartner invalidAnsprechpartner = ansprechpartnerWithoutLastModified();
		assertThatThrownBy(() -> service.update(invalidAnsprechpartner)).isInstanceOf(ResourceToUpdateNotValidException.class);
	}
	
	private Ansprechpartner validAnsprechpartnerForUpdate() {
		return AnsprechpartnerBuilder.aAnsprechpartner().withId(ID).withLastModified(LocalDateTime.now()).build();
	}
	
	private Ansprechpartner ansprechpartnerWithIdAndPersonId(UUID id, UUID personId) {
		return AnsprechpartnerBuilder.aAnsprechpartner().withId(id).withPersonId(personId).build();
	}
	
	private Ansprechpartner ansprechpartnerWithoutLastModified() {
		return AnsprechpartnerBuilder.aAnsprechpartner().withId(ID).build();
	}
	
	private String describe(AnsprechpartnerWithPerson ap) {
		return "<ap:" + ap.getAnsprechpartner().getId() + ",p:"
				+ ap.getPerson().map(SharedPerson::getOriginalId).orElse(UuidConstants.generateUUID(-1)) + ">";
	}
	
}
