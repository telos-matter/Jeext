package controllers.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import controllers.controller.core.Access;
import controllers.controller.core.util.BooleanEnum;
import models.core.Permission;

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
	
}
