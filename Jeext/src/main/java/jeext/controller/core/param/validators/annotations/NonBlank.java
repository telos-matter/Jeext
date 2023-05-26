package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.NonBlankValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} goes on {@link String} type of
 * {@link Param}s and makes sure the {@link String}
 * is not blank ({@link String#isBlank()})
 * 
 *  <p>The {@link Class} that actually implements this
 *  {@link Validator} is {@link NonBlankValidator}
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface NonBlank {

}
