package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.AfterDateTimeValidator;
import jeext.controller.core.param.validators.AfterDateValidator;
import jeext.controller.core.param.validators.AfterTimeValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} goes on {@link LocalDate}, 
 * {@link LocalTime} and {@link LocalDateTime} type
 * of {@link Param}s and makes sure the received value
 * is strictly after the specified {@link #value()}
 * 
 * <p>Of course the {@link #value()} has to be compatible with the
 * types' format; {@link DateTimeFormatter#ISO_LOCAL_DATE} in the case
 * of a {@link LocalDate} and so on..
 * 
 * <p>The {@link Class}es that actually implement this {@link Validator} are:
 * <ul>
 * <li>{@link AfterDateValidator}
 * <li>{@link AfterTimeValidator}
 * <li>{@link AfterDateTimeValidator}
 * </ul>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface After {

	/**
	 * The value to check against
	 * 
	 * @see After
	 */
	String value();

}
