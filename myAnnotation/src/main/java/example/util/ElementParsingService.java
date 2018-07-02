package example.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import example.domain.AnnotatedClazz;
import example.domain.MyAnnotation;
import net.karneim.pojobuilder.analysis.JavaModelAnalyzerUtil;

public class ElementParsingService {

	private ProcessingEnvironment processingEnv;
	private JavaModelAnalyzerUtil javaModelAnalyzerUtil;

	public ElementParsingService(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		javaModelAnalyzerUtil = new JavaModelAnalyzerUtil(processingEnv.getElementUtils(),
				processingEnv.getTypeUtils());
	}

	public AnnotatedClazz createAnnotatedClazz(final Element clazz) {
		MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
		PackageElement enclosingSourcePackage = (PackageElement) clazz.getEnclosingElement();
		TypeElement targetClass = extractTargetClass(annotation);
		javaModelAnalyzerUtil.getClassname(targetClass);
		javaModelAnalyzerUtil.getPackage(targetClass);
		return new AnnotatedClazz(//
				enclosingSourcePackage.getQualifiedName().toString(), //
				javaModelAnalyzerUtil.getPackage(targetClass), //
				javaModelAnalyzerUtil.getClassname(asTypeElement(clazz.asType())), //
				javaModelAnalyzerUtil.getClassname(targetClass)//
		);
	}

	@SuppressWarnings("unused")
	private TypeElement extractTargetClass(MyAnnotation annotation) {
		try {
			Class<?> target = annotation.target();
		} catch (MirroredTypeException e) {
			return asTypeElement(e.getTypeMirror());
		}

		return null;
	}

	private TypeElement asTypeElement(TypeMirror typeMirror) {
		Types TypeUtils = processingEnv.getTypeUtils();
		return (TypeElement) TypeUtils.asElement(typeMirror);
	}

}
