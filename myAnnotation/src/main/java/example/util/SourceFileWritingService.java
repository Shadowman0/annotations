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
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class SourceFileWritingService {

	private Configuration freemarkerCfg;
	private Filer filer;

	public SourceFileWritingService(final Filer filer) {
		this.filer = filer;
	}

	private void init() {
		freemarkerCfg = new Configuration(new Version(2, 3, 20));
		freemarkerCfg.setClassForTemplateLoading(this.getClass(), "/");
		freemarkerCfg.setDefaultEncoding("UTF-8");
		freemarkerCfg.setLocale(Locale.US);
		freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	}

	public void createFiles(final List<AnnotatedClazz> annotatedClazzes) {
		Objects.requireNonNull(annotatedClazzes);

		if (freemarkerCfg == null) {
			init();
		}
		Template template;
		String templateName = "templates/clazz.ftl";
		try {
			template = freemarkerCfg.getTemplate(templateName);
		} catch (IOException e) {
			Logger.error(e.getMessage());
			throw new RuntimeException("Could not load template '" + templateName + "'", e);
		}

		for (AnnotatedClazz clazz : annotatedClazzes) {

			Map<String, Object> modelMap = new HashMap<>();
			modelMap.put("packageName", clazz.getPackageName());
			modelMap.put("parentClazzName", clazz.getParentClazzName());

			for (String parameters : clazz.getParameters()) {
				modelMap.put("parameters", parameters);
				modelMap.put("generatedClazzName", "TestClazz");
				try {
					JavaFileObject jfo = filer.createSourceFile(clazz.getPackageName() + "." + "TestClazz");
					Writer writer = jfo.openWriter();
					template.process(modelMap, writer);
				} catch (IOException | TemplateException e) {
					Logger.error(e.getMessage());
				}
			}
		}
	}

}
