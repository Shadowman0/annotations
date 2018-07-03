package example.domain;

public class Parameter {

	private Class<String> clazz;
	private String name;

	public Parameter(Class<String> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
	}

	public Class<String> getClazz() {
		return clazz;
	}

	public void setClazz(Class<String> clazz) {
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		this.name = string;
	}

}
