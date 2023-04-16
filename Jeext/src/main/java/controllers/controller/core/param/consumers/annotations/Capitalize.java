package controllers.controller.core.param.consumers.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Capitalize {

	/**
	 * Force capitlazation
	 */
	boolean value() default false; // Force capitalization
	
}
