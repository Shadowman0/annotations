package example.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;

import example.domain.AnnotatedClazz;
import example.domain.MyAnnotation;

public class ElementParsingService {
	public AnnotatedClazz createAnnotatedClazz(final Element clazz) {
		MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
		PackageElement enclosingPackage = (PackageElement) clazz.getEnclosingElement();
		return new AnnotatedClazz(enclosingPackage.getQualifiedName().toString(), clazz.getSimpleName().toString(),
				annotation.parameters());
	}

}
