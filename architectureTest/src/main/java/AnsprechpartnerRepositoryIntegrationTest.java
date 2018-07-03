package de.etengo.eemweb.firma.integrationtest.ansprechpartner;

import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatDescriptionOf;
import static de.etengo.eemweb.commons.test.EtengoAssertions.assertThatEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.querydsl.core.BooleanBuilder;

import de.etengo.eemweb.commons.db.entity.errorhandling.ForeignKeyConstraintException;
import de.etengo.eemweb.commons.test.util.UuidConstants;
import de.etengo.eemweb.commons.testextension.integration.AbstractIntegrationTest;
import de.etengo.eemweb.commons.testextension.integration.CommitImmediately;
import de.etengo.eemweb.firma.ansprechpartner.Ansprechpartner;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerBuilder;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerFactory;
import de.etengo.eemweb.firma.ansprechpartner.AnsprechpartnerRepository;

public class AnsprechpartnerRepositoryIntegrationTest extends AbstractIntegrationTest {
	
	private static final UUID ID_1 = UuidConstants.ID_1;
	private static final UUID ID_3 = UuidConstants.ID_3;
	private static final UUID STANDORT_ID = UuidConstants.ID_1;
	private static final UUID INVALID_FK = UuidConstants.generateUUID(999L);
	private static final UUID PERSON_ID = UuidConstants.ID_1;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private AnsprechpartnerRepository repository;
	
	@Test
	public void findComplete_ById() {
		Ansprechpartner result = repository.findById(ID_1).get();
		assertThatDescriptionOf(result).isEqualTo(getClass().getResource("ansprechpartnerComplete.txt"));
	}
	
	@Test
	public void findAll() {
		Stream<Ansprechpartner> ansprechpartnerStream = repository.findAll();
		assertThat(ansprechpartnerStream).isInstanceOf(Stream.class);
		
		assertThat(ansprechpartnerStream.collect(Collectors.toList()).size()).isEqualTo(5);
	}
	
	@Test
	public void findMinimal_ById() {
		Ansprechpartner result = repository.findById(ID_3).get();
		assertThatDescriptionOf(result).isEqualTo(getClass().getResource("ansprechpartnerMinimal.txt"));
	}
	
	@Test
	public void findByStandortId() {
		List<Ansprechpartner> result = repository.findByStandortId(STANDORT_ID);
		assertThat(result).extracting(Ansprechpartner::getId).contains(UuidConstants.ID_2);
	}
	
	@Test
	public void findByPersonId() {
		assertThatDescriptionOf(repository.findByPersonId(PERSON_ID).get()).isEqualTo(getClass().getResource("ansprechpartnerComplete.txt"));
	}
	
	@Test
	public void save() {
		Ansprechpartner saved = repository.save(validNewAnsprechpartner().build());
		flushAndClear();
		
		Ansprechpartner retrieved = repository.findById(saved.getId()).get();
		
		assertThatEntity(retrieved).isEqualToComparingDomainFields(validNewAnsprechpartner().build()).wasRecentlyInserted();
	}
	
	@Test
	@CommitImmediately
	public void create_AnsprechpartnerWithInvalidStandortFK_ShouldThrowException() {
		Ansprechpartner ansprechpartner = validNewAnsprechpartner().withStandortId(INVALID_FK).build();
		
		assertThatThrownBy(() -> repository.save(ansprechpartner)).isInstanceOf(ForeignKeyConstraintException.class)
				.hasMessage("Ungültige Referenz auf Standort");
	}
	
	@Test
	@CommitImmediately
	public void save_AnsprechpartnerWithJPAConstraintBroken_ShouldThrowException() {
		Ansprechpartner ansprechpartner = validNewAnsprechpartner().build();
		ansprechpartner.setTelefonnummer(StringUtils.repeat("0", 41));
		
		assertThatThrownBy(() -> repository.save(ansprechpartner)).isInstanceOf(NonTransientDataAccessException.class);
	}
	
	@Test
	public void findAnsprechpartnerByFirma() {
		List<Ansprechpartner> result = repository.findByFirmaId(ID_1);
		assertThat(result).extracting(Ansprechpartner::getId).containsExactlyInAnyOrder(UuidConstants.ID_1, UuidConstants.ID_2, UuidConstants.ID_5);
	}
	
	@Test
	public void update_WithIdAndLastModified_UpdatesAnsprechpartner() {
		long initialNumberOfRecords = numberOfRecords();
		Ansprechpartner savedAnsprechpartner = repository.save(validNewAnsprechpartner().withVorgesetzterId((UUID) null).build());
		assertThat(savedAnsprechpartner.getVorgesetzterId()).isEmpty();
		assertThat(numberOfRecords()).isEqualTo(initialNumberOfRecords + 1);
		
		Ansprechpartner updateObject = validNewAnsprechpartner().withId(savedAnsprechpartner.getId())
				.withLastModified(savedAnsprechpartner.getLastModified()).build();
		updateObject.setVorgesetzterId(UuidConstants.ID_1);
		repository.save(updateObject);
		
		Ansprechpartner retrievedEntity = repository.findById(savedAnsprechpartner.getId()).get();
		assertThat(retrievedEntity.getVorgesetzterId()).hasValue(UuidConstants.ID_1);
		assertThat(retrievedEntity.getId()).isEqualTo(savedAnsprechpartner.getId());
		
		assertThat(numberOfRecords()).isEqualTo(initialNumberOfRecords + 1);
	}
	
	@Test
	public void update_WithIdAndLastModifiedChanged_ThrowsOptimisticLockException() {
		Ansprechpartner savedAnsprechpartner = repository.save(validNewAnsprechpartner().build());
		
		Ansprechpartner updateObject = validNewAnsprechpartner().withId(savedAnsprechpartner.getId())
				.withLastModified(savedAnsprechpartner.getLastModified().plusDays(1)).build();
		updateObject.setVorgesetzterId(UuidConstants.ID_1);
		flushAndClear();
		assertThatThrownBy(() -> repository.save(updateObject)).isInstanceOf(ObjectOptimisticLockingFailureException.class);
	}
	
	@Test
	public void delete() {
		repository.delete(ID_1);
		
		assertThat(repository.findById(ID_1)).isEmpty();
	}
	
	private long numberOfRecords() {
		return repository.count(new BooleanBuilder());
	}
	
	private AnsprechpartnerBuilder validNewAnsprechpartner() {
		return AnsprechpartnerFactory.aCompleteAnsprechpartnerBuilder().withStandortId(STANDORT_ID).withId(null).withLastModified(null);
	}
}
