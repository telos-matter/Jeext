package jeext.controller.core.param.consumers.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.consumers.CapitalizeConsumer;
import jeext.controller.core.param.consumers.Consumer;
import jeext.util.Strings;

/**
 * <p>This {@link Consumer} type of {@link Annotation}
 * goes on {@link String} type {@link Param}s
 * <p>It capitalizes what ever value received
 * in the request
 * <p>The {@link Class} that actually implements this
 * {@link Consumer} is {@link CapitalizeConsumer}
 * 
 * @see #forceCapitalize()
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Capitalize {

	/**
	 * <p>Specify whether or not to make sure the
	 * returned {@link String} is really capitalized,
	 * meaning only the first letter is in upperCase
	 * while the rest is not. Or to simply just upperCase
	 * the first letter and leave the rest as it is
	 * <p>By default it is <code>false</code>,
	 * it only capitalizes the first letter
	 * 
	 * @see Strings#capitalize(String)
	 * @see Strings#forceCapitalize(String)
	 */
	boolean forceCapitalize() default false;
	
}
