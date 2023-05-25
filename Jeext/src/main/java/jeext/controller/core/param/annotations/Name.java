package jeext.controller.core.param.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.param.Param;

/**
 * <p>Specifies the name by which to retrieve the {@link Param}
 * from the request
 * <p>By default (if this {@link Annotation} is not used)
 * the name by which the {@link Param} is retrieved
 * is that of {@link Parameter}
 * in the {@link Mapping} {@link Method}
 * <p><b>Know that</b> in order for the default thing to work
 * you need to specify the `-parameters` options to the compiler
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Name {
	
	/**
	 * The name by which to retrieve the {@link Param}
	 * from the incoming request
	 * 
	 * @see Name
	 */
	String value();
	
}
