package jeext.controller.core.param.validators.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.util.List;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.consumers.annotations.Default;
import jeext.controller.core.param.validators.RequiredValidator;
import jeext.controller.core.param.validators.Validator;

/**
 * <p>This {@link Validator} indicate that the {@link Param}
 * is required and expected to be found in the
 * request and to be compatible with the {@link Param}s' type
 * <p>It can go on any type of {@link Param}s, but in the case
 * of {@link Array} and {@link List} type of {@link Param}s
 * it only checks if the request contains a parameter with the
 * same name, and not whether it is compatible or not. Use
 * {@link Min} or {@link Max} to check {@link Array} and
 * {@link List} type of {@link Param}s
 * 
 * <p><b>Know that</b> this {@link Validator} is on every
 * {@link Param} by default, unless if the {@link Param}
 * is annotated with {@link Default}
 * or {@link Composed}
 * 
 * <p><b>Know that</b> this {@link Validator} is mutually exclusive with
 * {@link Default} and {@link Composed}
 * 
 * <p>The class that actually implements this {@link Validator}
 * is {@link RequiredValidator}
 * 
 * @see Param
 * @see Default
 * @see Composed
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Required {

	/**
	 * Specifies whether this param is required or not
	 * 
	 * @see Required
	 */
	boolean value();
	
}
