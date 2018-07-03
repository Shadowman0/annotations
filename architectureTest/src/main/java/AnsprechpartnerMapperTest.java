package de.etengo.eemweb.firma.ansprechpartner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import de.etengo.eemweb.firma.api.AnsprechpartnerDTO;
import de.etengo.eemweb.firma.api.AnsprechpartnerDTOFactory;

@RunWith(MockitoJUnitRunner.class)
public class AnsprechpartnerMapperTest {
	
	private static final UUID NEW_ID = UUID.fromString("8404a048-570f-4544-b781-745123c933ae");
	
	@InjectMocks
	private AnsprechpartnerMapper ansprechpartnerMapper;
	
	@Test
	public void asDto() {
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aCompleteAnsprechpartner();
		AnsprechpartnerDTO expected = AnsprechpartnerDTOFactory.aCompleteAnsprechpartnerDTOBuilder().build();
		
		assertThat(ansprechpartnerMapper.asDto(ansprechpartner)).isEqualTo(expected);
	}
	
	@Test
	public void asDto_minimal() {
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aMinimalAnsprechpartnerBuilder().build();
		AnsprechpartnerDTO expected = AnsprechpartnerDTOFactory.aMinimalAnsprechpartnerDTOBuilder().build();
		
		assertThat(ansprechpartnerMapper.asDto(ansprechpartner)).isEqualTo(expected);
	}
	
	@Test
	public void fromNewDTO_IdAndLastModifiedIsNotMapped() {
		AnsprechpartnerDTO ansprechpartnerDto = AnsprechpartnerDTOFactory.aCompleteAnsprechpartnerDTOBuilder().build();
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aCompleteAnsprechpartnerBuilder().withId(null).withLastModified(null).build();
		
		assertThat(ansprechpartnerMapper.fromNewDTO(ansprechpartnerDto)).isEqualToComparingFieldByFieldRecursively(ansprechpartner);
	}
	
	@Test
	public void fromUpdatedDTO_IdOverwritten() {
		AnsprechpartnerDTO ansprechpartnerDto = AnsprechpartnerDTOFactory.aCompleteAnsprechpartnerDTOBuilder().build();
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aCompleteAnsprechpartner();
		ansprechpartner.setId(NEW_ID);
		
		assertThat(ansprechpartnerMapper.fromUpdatedDTO(ansprechpartnerDto, ansprechpartner.getId()))
				.isEqualToComparingFieldByFieldRecursively(ansprechpartner);
	}
	
	@Test
	public void asDto_ansprechpartnerCorrectMapping() {
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aCompleteAnsprechpartner();
		AnsprechpartnerDTO ansprechpartnerDto = ansprechpartnerMapper.asDto(ansprechpartner);
		assertThat(ansprechpartnerDto).isEqualTo(AnsprechpartnerDTOFactory.aCompleteAnsprechpartnerDTOBuilder().build());
	}
	
	@Test
	public void asDto_ansprechpartner_minimal() {
		Ansprechpartner ansprechpartner = AnsprechpartnerFactory.aMinimalAnsprechpartnerBuilder().build();
		AnsprechpartnerDTO ansprechpartnerDto = ansprechpartnerMapper.asDto(ansprechpartner);
		assertThat(ansprechpartnerDto).isEqualTo(AnsprechpartnerDTOFactory.aMinimalAnsprechpartnerDTOBuilder().build());
	}
	
}
