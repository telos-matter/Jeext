package jeext.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.Access;
import jeext.controller.core.util.BooleanEnum;
import jeext.models_core.Permission;

/**
 * <p>A mapping for POST type request.
 * <p>This should be contained inside
 * a {@link WebController}
 * 
 * @author telos_matter
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface PostMapping {

	String value () default "";
	
	Access access () default Access.DEFAULT;
	Permission [] permissions () default {};
	BooleanEnum anyPermission () default BooleanEnum.NULL;
	
	boolean independent () default false;
	
}
