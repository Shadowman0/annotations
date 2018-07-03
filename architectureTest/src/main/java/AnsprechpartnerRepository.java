package de.etengo.eemweb.firma.ansprechpartner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.Repository;

import com.querydsl.core.types.Predicate;

import de.etengo.eemweb.commons.db.entity.ExistingUuid;
import de.etengo.eemweb.commons.db.entity.Saving;

public interface AnsprechpartnerRepository extends Repository<Ansprechpartner, UUID>, QueryDslPredicateExecutor<Ansprechpartner>,
		AnsprechpartnerRepositoryExtension, ExistingUuid, Saving<Ansprechpartner> {
	
	@Override
	List<Ansprechpartner> findAll(Predicate predicate);
	
	Stream<Ansprechpartner> findAll();
	
	Optional<Ansprechpartner> findById(UUID id);
	
	List<Ansprechpartner> findByStandortId(UUID standortId);
	
	Optional<Ansprechpartner> findByPersonId(UUID personId);
	
	void delete(UUID id);
	
}
