package jeext.controller.core.param.consumers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jeext.controller.core.param.Composer;
import jeext.controller.core.param.Param;
import jeext.controller.core.param.consumers.annotations.Capitalize;
import jeext.controller.core.param.consumers.annotations.Default;
import jeext.controller.core.param.consumers.annotations.LowerCase;
import jeext.controller.core.param.consumers.annotations.UpperCase;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>{@link Consumer} type of {@link Annotation}s are those that
 * operate on the received parameters 
 * <p>This {@link FunctionalInterface} provides the
 * base abstract {@link Method} all implementations of the 
 * {@link Consumer}s' {@link Annotation}s should define
 * <p>The {@link Annotation}s that are of type {@link Consumer} are:
 * <ul>
 * <li>{@link Default}
 * <li>{@link Capitalize}
 * <li>{@link UpperCase}
 * <li>{@link LowerCase}
 * </ul>
 * 
 * @implNote The benefits that come with
 * using a {@link FunctionalInterface}
 * is not used at all at the moment
 * 
 * @see Param
 * @see Validator
 * @see Composer
 */
@FunctionalInterface
public interface Consumer {

	/**
	 * @see Consumer
	 */
	public Object consume (Object object);
	
}
