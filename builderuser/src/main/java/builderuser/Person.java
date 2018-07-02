package builderuser;

import annotationTest.MyBuilder;

public class Person {
	private String name;
	private int alter;

	public String getName() {
		return name;
	}

	@MyBuilder
	public void setName(String name) {
		this.name = name;
	}

	public int getAlter() {
		return alter;
	}

	@MyBuilder
	public void setAlter(int alter) {
		this.alter = alter;
	}
}
