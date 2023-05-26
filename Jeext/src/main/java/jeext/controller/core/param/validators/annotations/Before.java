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
import jeext.controller.core.param.validators.BeforeDateTimeValidator;
import jeext.controller.core.param.validators.BeforeDateValidator;
import jeext.controller.core.param.validators.BeforeTimeValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} goes on {@link LocalDate}, 
 * {@link LocalTime} and {@link LocalDateTime} type
 * of {@link Param}s and makes sure the received value
 * is strictly before the specified {@link #value()}
 * 
 * <p>Of course the {@link #value()} has to be compatible with the
 * types' format; {@link DateTimeFormatter#ISO_LOCAL_DATE} in the case
 * of a {@link LocalDate} and so on..
 * 
 * <p>The {@link Class}es that actually implement this {@link Validator} are:
 * <ul>
 * <li>{@link BeforeDateValidator}
 * <li>{@link BeforeTimeValidator}
 * <li>{@link BeforeDateTimeValidator}
 * </ul>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Before {

	/**
	 * The value to check against
	 * 
	 * @see Before
	 */
	String value();
	
}
