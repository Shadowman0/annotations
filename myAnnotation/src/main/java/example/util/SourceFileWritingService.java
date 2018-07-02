package example.util;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import example.domain.AnnotatedClazz;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.Version;

public class SourceFileWritingService {

	private Configuration freemarkerCfg;
	private Filer filer;

	public SourceFileWritingService(final Filer filer) {
		this.filer = filer;
		freemarkerCfg = new Configuration(new Version(2, 3, 20));
		freemarkerCfg.setClassForTemplateLoading(this.getClass(), "/");
		freemarkerCfg.setDefaultEncoding("UTF-8");
		freemarkerCfg.setLocale(Locale.US);
		freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	private void createFiles(final List<AnnotatedClazz> annotatedClazzes) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateName = "templates/mapper.ftl";
		Template template = freemarkerCfg.getTemplate(templateName);
		for (AnnotatedClazz clazz : annotatedClazzes) {
			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("packageName", clazz.getSourcePackageName());
			modelMap.put("sourcePackageName", clazz.getSourcePackageName());
			modelMap.put("targetPackageName", clazz.getTargetPackageName());
			modelMap.put("source", clazz.getSource());
			modelMap.put("target", clazz.getTarget());
			String generatedClazzName = clazz.getSource() + "To" + clazz.getTarget() + "Mapper";
			modelMap.put("generatedClazzName", generatedClazzName);
			JavaFileObject jfo = filer.createSourceFile(clazz.getSourcePackageName() + "." + generatedClazzName);
			Writer writer = jfo.openWriter();
			template.process(modelMap, writer);
		}
	}

	public void createFilesSafely(List<AnnotatedClazz> annotatedClazzes) {
		Objects.requireNonNull(annotatedClazzes);
		try {
			createFiles(annotatedClazzes);
		} catch (IOException | TemplateException e) {
			Logger.error(e.getMessage());
		}

	}

}
