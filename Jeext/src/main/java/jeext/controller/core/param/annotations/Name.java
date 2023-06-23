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
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.model.Model;

/**
 * <p>Specifies the name by which to retrieve the {@link Param}
 * from the request
 * <p><b>By default</b> (if this {@link Annotation} is not used)
 * the name by which the {@link Param} is retrieved
 * is that of {@link Parameter}
 * in the {@link Mapping} {@link Method}
 * <p>In the case of {@link Model} type
 * of {@link Param}s ({@link Composed} or not)
 * it refers to the id ({@link MID})
 * <p><b>Know that</b> in order for the default case to work
 * you need to specify the `-parameters` options to the compiler
 * 
 * @see Composed
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
