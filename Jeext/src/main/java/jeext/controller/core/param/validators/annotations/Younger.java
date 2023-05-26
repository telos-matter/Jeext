package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.Validator;
import jeext.controller.core.param.validators.YoungerValidator;
import jeext.util.Dates.PeriodHolder;

/**
 * <p>This {@link Validator} goes on {@link LocalDate} type of
 * {@link Param}s and makes sure the {@link LocalDate} value
 * is younger
 * than the specified {@link PeriodHolder} {@link #value()}.
 * <p>Meaning that
 * {@link LocalDate#now()} minus the received {@link LocalDate}
 * is less than the specified  {@link PeriodHolder}
 * <p>For example if you require that only those younger than
 * the age of
 * 60 are be able to sign-up into your website, you can ask for their
 * birthdate and check using the value `60,0,0` 
 * 
 * <p>The {@link Class} that actually implements this {@link Validator}
 * is {@link YoungerValidator}
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Younger {

	/**
	 * The {@link PeriodHolder} value
	 * to check against
	 */
	String value();

}
