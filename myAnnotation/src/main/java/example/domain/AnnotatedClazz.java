package example.domain;

public class AnnotatedClazz {

	private String sourcePackageName;
	private String targetPackageName;

	private String source;
	private String target;

	public AnnotatedClazz(String sourcePackageName, String targetPackageName, String source, String target) {
		this.sourcePackageName = sourcePackageName;
		this.targetPackageName = targetPackageName;
		this.source = source;
		this.target = target;
	}

	public String getSourcePackageName() {
		return sourcePackageName;
	}

	public void setSourcePackageName(String sourcePackageName) {
		this.sourcePackageName = sourcePackageName;
	}

	public String getTargetPackageName() {
		return targetPackageName;
	}

	public void setTargetPackageName(String targetPackageName) {
		this.targetPackageName = targetPackageName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
