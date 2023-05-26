package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.RegexValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} goes on {@link String} type of
 * {@link Param}s and makes sure the {@link String}
 * only contains alphabetic characters (a-Z)
 * 
 * <p>The {@link Class} that actually implements this
 * {@link Validator} is {@link RegexValidator}
 *  
 * @see #allowUnderscore() 
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Alphabetic {

	/**
	 * <p>Specify whether or not to allow
	 * underscores (`_`) too
	 * <p>By default it is false
	 * @see Alphabetic
	 */
	boolean allowUnderscore () default false;
	
}
