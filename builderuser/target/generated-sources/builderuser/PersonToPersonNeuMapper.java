package builderuser;
 
import builderuser.Person;
import builderuser.PersonNeu;
 
public class PersonToPersonNeuMapper {

public static PersonNeu map(Person from){
	PersonNeu result = new PersonNeu();
		result.setVorname(from.getVorname());
		result.setNachname(from.getNachname());
		result.setStrasse(from.getStrasse());
		result.setHausnummer(from.getHausnummer());
   	return result;
   	}

}