package de.etengo.eemweb.firma.ansprechpartner;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.etengo.eemweb.commons.db.entity.Entities;
import de.etengo.eemweb.firma.person.PersonService;

@Component
public class AnsprechpartnerService {
	
	private static final String ERROR_ANSPRECHPARTNER_NOT_FOUND = "Ansprechpartner nicht gefunden";
	
	@Autowired
	private AnsprechpartnerRepository repository;
	
	@Autowired
	private AnsprechpartnerPublisher messaging;
	
	@Autowired
	private PersonService personService;
	
	public Ansprechpartner get(UUID id) {
		return Entities.getOrThrow(repository.findById(id), ERROR_ANSPRECHPARTNER_NOT_FOUND);
	}
	
	public Ansprechpartner getByPersonId(UUID personId) {
		return Entities.getOrThrow(repository.findByPersonId(personId), ERROR_ANSPRECHPARTNER_NOT_FOUND);
	}
	
	public List<AnsprechpartnerWithPerson> getAnsprechpartnerWithPersonForAnsprechpartner(List<Ansprechpartner> ansprechpartner) {
		return ansprechpartner.stream().map(ap -> new AnsprechpartnerWithPerson(personService.get(ap.getPersonId()), ap))
				.collect(Collectors.toList());
	}
	
	public Ansprechpartner update(Ansprechpartner ansprechpartner) {
		ansprechpartner.checkValidForUpdate(repository);
		ansprechpartner.setPersonId(get(ansprechpartner.getId()).getPersonId());
		return saveAndNotify(ansprechpartner);
	}
	
	public Ansprechpartner create(Ansprechpartner ansprechpartner) {
		return saveAndNotify(ansprechpartner);
	}
	
	private Ansprechpartner saveAndNotify(Ansprechpartner entity) {
		Ansprechpartner result = repository.save(entity);
		messaging.publish(new AnsprechpartnerMessageMapper().asMessage(result));
		return result;
	}
	
}
