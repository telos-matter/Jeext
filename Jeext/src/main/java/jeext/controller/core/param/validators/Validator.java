package jeext.controller.core.param.validators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.Controller;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.param.Composer;
import jeext.controller.core.param.Param;
import jeext.controller.core.param.consumers.Consumer;
import jeext.controller.core.param.validators.annotations.After;
import jeext.controller.core.param.validators.annotations.Alphabetic;
import jeext.controller.core.param.validators.annotations.Alphanumeric;
import jeext.controller.core.param.validators.annotations.Before;
import jeext.controller.core.param.validators.annotations.Email;
import jeext.controller.core.param.validators.annotations.Max;
import jeext.controller.core.param.validators.annotations.Min;
import jeext.controller.core.param.validators.annotations.NonBlank;
import jeext.controller.core.param.validators.annotations.Older;
import jeext.controller.core.param.validators.annotations.Regex;
import jeext.controller.core.param.validators.annotations.Required;
import jeext.controller.core.param.validators.annotations.Younger;

/**
 * <p>{@link Validator} type of {@link Annotation}s are those that
 * check and validate the incoming request parameter to
 * make sure they validates certain criteria, that
 * the developer knows that they should normally fit, before
 * giving them to the {@link Param}s and call the
 * {@link Mapping}. If they don't,
 * an {@link InvalidParameter} {@link Exception} is thrown
 * which in turn makes the {@link Controller}
 * send a {@link HttpServletResponse#SC_BAD_REQUEST}
 * error to the requester
 * <p>This {@link FunctionalInterface} provides the
 * base abstract {@link Method} all implementations of the 
 * {@link Validator}s' {@link Annotation}s should define
 * <p>The {@link Annotation}s that are of type {@link Validator} are:
 * <ul> 
 * <li>{@link Required}
 * <li>{@link Min}
 * <li>{@link Max}
 * <li>{@link Email}
 * <li>{@link Alphabetic}
 * <li>{@link Alphanumeric}
 * <li>{@link Regex}
 * <li>{@link NonBlank}
 * <li>{@link Before}
 * <li>{@link After}
 * <li>{@link Younger}
 * <li>{@link Older}
 * </ul>
 * 
 * <p><b>Know that:</b> All {@link Validator}s (except {@link Required})
 * allow and validate <code>null</code>, meaning
 * they won't check for their condition or criteria
 * if there is no parameter in the first place
 * 
 * @implNote The benefits that come with
 * using a {@link FunctionalInterface}
 * is not used at all at the moment
 * 
 * @see Param
 * @see Consumer
 * @see Composer
 */
@FunctionalInterface
public interface Validator {
	
	/**
	 * @see Validator
	 */
	public boolean validate (Object object);
	
}
