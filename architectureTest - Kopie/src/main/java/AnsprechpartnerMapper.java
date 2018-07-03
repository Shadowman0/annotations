package de.etengo.eemweb.firma.ansprechpartner;

import org.springframework.stereotype.Component;

import de.etengo.eemweb.commons.core.dto.ToDtoMapper;
import de.etengo.eemweb.commons.db.entity.mapper.DtoToVersionedEntityWithUuidMapper;
import de.etengo.eemweb.firma.api.AnsprechpartnerDTO;

@Component
public class AnsprechpartnerMapper
		implements ToDtoMapper<Ansprechpartner, AnsprechpartnerDTO>, DtoToVersionedEntityWithUuidMapper<AnsprechpartnerDTO, Ansprechpartner> {
	
	@Override
	public AnsprechpartnerDTO asDto(Ansprechpartner entity) {
		return new AnsprechpartnerDTO(entity.getId(), entity.getPersonId(), entity.getLastModified(), entity.getStandortId(),
				entity.getTelefonnummer(), entity.getEmailadresse(), entity.getPosition(), entity.getAbteilung(), entity.getVorgesetzterId());
	}
	
	@Override
	public Ansprechpartner fromNewDTO(AnsprechpartnerDTO dto) {
		Ansprechpartner entity = new Ansprechpartner();
		entity.setPersonId(dto.getPersonId());
		entity.setStandortId(dto.getStandortId());
		entity.setTelefonnummer(dto.getTelefonnummer());
		entity.setEmailadresse(dto.getEmailadresse());
		entity.setPosition(dto.getPosition());
		entity.setAbteilung(dto.getAbteilung());
		entity.setVorgesetzterId(dto.getVorgesetzterId());
		return entity;
	}
	
}
