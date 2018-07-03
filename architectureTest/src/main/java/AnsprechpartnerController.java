package de.etengo.eemweb.firma.ansprechpartner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.etengo.eemweb.commons.core.api.ApiErrorDTO;
import de.etengo.eemweb.commons.core.serialization.ObjectMapping;
import de.etengo.eemweb.commons.domainconstants.RollenConstants;
import de.etengo.eemweb.commons.security.Roles;
import de.etengo.eemweb.commons.security.authentication.AuthenticationInfoService;
import de.etengo.eemweb.commons.validation.EtengoValidationException;
import de.etengo.eemweb.commons.validation.errorhandling.EtengoValidator;
import de.etengo.eemweb.firma.FirmaConfiguration;
import de.etengo.eemweb.firma.api.AnsprechpartnerAnlegenNestedDTO;
import de.etengo.eemweb.firma.api.AnsprechpartnerDTO;
import de.etengo.eemweb.firma.api.PersonIdDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api
@RequestMapping(value = FirmaConfiguration.SERVICE_PREFIX + "/ansprechpartner", produces = { MediaType.APPLICATION_JSON_VALUE,
		MediaType.APPLICATION_XML_VALUE })
public class AnsprechpartnerController {
	
	private static final String ERROR_ANSPRECHPARTNER_NOT_FOUND = "Ansprechpartner nicht gefunden";
	
	@Autowired
	private AnsprechpartnerMapper mapper;
	
	@Autowired
	private AnsprechpartnerAnlegenMapper ansprechpartnerAnlegenMapper;
	
	@Autowired
	private PersonSecuredInternalClient personClient;
	
	@Autowired
	private AnsprechpartnerService service;
	
	@Autowired
	private StandortExisting standortExisting;
	
	@Autowired
	private AuthenticationInfoService authenticationInfoService;

	@Autowired
	private EtengoValidator dtoValidator;
	
	@ApiOperation(value = "Informationen eines Ansprechpartners", notes = "Liefert die Daten des Ansprechpartners mit der angegebenen ID zurück.")
	@ApiResponses({ @ApiResponse(code = 404, message = ERROR_ANSPRECHPARTNER_NOT_FOUND) })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public AnsprechpartnerDTO get(@PathVariable("id") UUID id) {
		Ansprechpartner entity = service.get(id);
		return mapper.asDto(entity);
	}

	//TODO swels: DTO instead of String for Controller arguments
	@ApiOperation(value = "Ansprechpartner anlegen", notes = "Legt einen neuen Ansprechpartner mit den angegebenen Daten an und liefert das Ergebnis zurück (Ansprechpartnerdaten + ID). Die ID im Request-Objekt wird ignoriert.")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request. Details siehe Message.", response = ApiErrorDTO.class) })
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Secured(Roles.FIRMA_WRITE)
	public PersonIdDTO create(@RequestBody String ansprechpartnerAnlegenJson) {
		AnsprechpartnerAnlegenNestedDTO dto = parseJson(ansprechpartnerAnlegenJson);
		dtoValidator.validate(dto);
		standortExisting.exists(dto.getAnsprechpartner().getStandortId());
		
		String jsonWithRolle = new JsonWithRolleExtender().extendJsonWithRolle(ansprechpartnerAnlegenJson, RollenConstants.ANSPRECHPARTNER);
		PersonIdDTO result = personClient.createPerson(authenticationInfoService.getCompleteAuthHeader(),
				jsonWithRolle.getBytes(StandardCharsets.UTF_8));
		
		service.create(
				ansprechpartnerAnlegenMapper.fromNewAnsprechpartnerAnlegenDtoWithPersonId(result.getPerson().getId(), dto.getAnsprechpartner()));
		return result;
	}
	
	@ApiOperation(value = "Ansprechpartner aktualisieren", notes = "Aktualisiert einen Ansprechpartner mit den angegebenen Daten und liefert das Ergebnis zurück (Ansprechpartnerdaten + ID). Die ID und lastModified im Request-Objekt wird genutzt um das Ziel Objekt zu identifizieren.")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad Request. Details siehe Message.", response = ApiErrorDTO.class),
			@ApiResponse(code = 409, message = "Das Aktualisieren eines Ansprechpartners, der zwischenzeitlich von einem anderen Benutzer geändert wurde,ist\n"
					+ " nicht erlaubt.", response = ApiErrorDTO.class) })
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@Secured(Roles.FIRMA_WRITE)
	public AnsprechpartnerDTO update(@Valid @RequestBody AnsprechpartnerDTO ansprechpartnerDto, @PathVariable("id") UUID id) {
		Ansprechpartner ansprechpartner = mapper.fromUpdatedDTO(ansprechpartnerDto, id);
		return mapper.asDto(service.update(ansprechpartner));
	}
	
	private AnsprechpartnerAnlegenNestedDTO parseJson(String ansprechpartnerAnlegenJson) {
		try {
			ObjectMapper jsonBuilder = ObjectMapping.jsonBuilder();
			return jsonBuilder.readValue(ansprechpartnerAnlegenJson, AnsprechpartnerAnlegenNestedDTO.class);
		} catch (IOException e) {
			throw new EtengoValidationException("ungültiges Json? " + ansprechpartnerAnlegenJson, e);
		}
	}
	
}
