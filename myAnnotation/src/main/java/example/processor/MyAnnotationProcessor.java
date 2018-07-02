package example.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import example.domain.AnnotatedClazz;
import example.domain.MyAnnotation;
import example.util.ElementParsingService;
import example.util.Logger;
import example.util.SourceFileWritingService;

@SupportedAnnotationTypes("com.queomedia.generator.example.annotation.MyAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(value = MyAnnotationProcessor.class)
public class MyAnnotationProcessor extends AbstractProcessor {
	private ElementParsingService elementParser;
	private SourceFileWritingService sourceFileWriter;

	@Override
	public void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.elementParser = new ElementParsingService();
		this.sourceFileWriter = new SourceFileWritingService(processingEnv.getFiler());
		Logger.init(processingEnv.getMessager());
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);
		List<AnnotatedClazz> parsedClazzes = new ArrayList<>();
		for (Element element : annotatedElements) {
			AnnotatedClazz clazz = this.elementParser.createAnnotatedClazz(element);
			parsedClazzes.add(clazz);
		}

		this.sourceFileWriter.createFiles(parsedClazzes);
		return false;
	}
}
