package example.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface MyAnnotation {
	Class<?> target();
}
