package controllers.controller.core.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Alphabetic {

	/**
	 * Allow underscore
	 */
	boolean value () default false;
	
}
