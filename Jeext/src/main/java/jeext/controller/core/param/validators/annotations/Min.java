package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.List;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.MinArrayValidator;
import jeext.controller.core.param.validators.MinListValidator;
import jeext.controller.core.param.validators.MinStringValidator;
import jeext.controller.core.param.validators.MinValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} goes on a couple of {@link Param}s
 * and makes sure that a quantity is greater than (or equal to, {@link #strict()})
 * the specified {@link #value()}
 * 
 * <p>The types of {@link Param}s that this {@link Validator}
 * can go on and the quantity it checks for are:
 * <ul>
 * <li>{@link Number}
 * ({@link Integer}, {@link Float}, {@link Double},
 * {@link Long}, {@link Short}, {@link Byte}) - 
 * The {@link #value()} has to be compatible
 * with the type
 * <li>{@link String} - Checks the {@link String#length()}. The
 * {@link #value()} has to be a positive or null {@link Integer}
 * <li>{@link Array} kind of {@link Param}s - Checks for how many
 * elements of the {@link Array} are not <code>null</code>, meaning
 * how many of the received parameters are compatible with the
 * {@link Array}s' type. The {@link #value()} has to be a positive
 * or null {@link Integer}
 * <li>{@link List} kind of {@link Param}s - Checks for how many
 * elements of the {@link List} are not <code>null</code>, meaning
 * how many of the received parameters are compatible with the
 * {@link List}s' type. The {@link #value()} has to be a positive
 * or null {@link Integer}
 * </ul>
 * 
 * <p>The {@link Class}es that actually implement this {@link Validator}
 * are:
 * <ul>
 * <li>{@link MinValidator}
 * <li>{@link MinStringValidator}
 * <li>{@link MinArrayValidator}
 * <li>{@link MinListValidator}
 * </ul>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Min {

	/**
	 * The value to check against
	 */
	double value();
	
	/**
	 * <p>Whether the quantity has to be 
	 * greater than or equal to the {@link #value()}
	 * (<code>false</code>), or strictly greater than the
	 * {@link #value()} (<code>true</code>)
	 * <p>By default it is <code>false</code>
	 */
	boolean strict() default false;
	
}
