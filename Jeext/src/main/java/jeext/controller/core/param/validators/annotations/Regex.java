package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.RegexValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} goes on {@link String} type
 * of {@link Param}s and makes sure
 * the received {@link String} contains at least 1 match
 * with the regex {@link #value()}
 * <p><b>Keep in mind that</b> if you want to make sure
 * that the {@link String} only contains certain
 * specified characters and nothing else
 * you need to include the `^` and `$`special characters
 *
 * <p>The {@link Class} that actually implements this
 * {@link Validator} is {@link RegexValidator}
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Regex {

	/**
	 * <p>The regex value to check for
	 * <p>Of course has to be a valid regex {@link Pattern}
	 * @see Regex
	 */
	String value();
	
}
