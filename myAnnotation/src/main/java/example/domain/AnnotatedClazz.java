package example.domain;

public class AnnotatedClazz {

	private String packageName;

	private String parentClazzName;

	private String[] parameters;

	public AnnotatedClazz(final String packageName, final String parentClazzName, final String[] parameters) {
		this.packageName = packageName;
		this.parentClazzName = parentClazzName;
		this.parameters = parameters;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getParentClazzName() {
		return parentClazzName;
	}

	public String[] getParameters() {
		return parameters;
	}

}
