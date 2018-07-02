package builderuser;

public class PersonBuilder {

    private Person object = new Person();

    public Person build() {
        return object;
    }

    public PersonBuilder setName(java.lang.String value) {
        object.setName(value);
        return this;
    }

    public PersonBuilder setAlter(int value) {
        object.setAlter(value);
        return this;
    }

}
