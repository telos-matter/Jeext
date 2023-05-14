package jeext.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.Access;
import jeext.controller.core.util.BooleanEnum;
import jeext.models_core.Permission;
import models.User;

// TODO should add and specify that anyPermission should be null if there is not permissions?
// TODO add an independent variable to indicate whether it should inherite from the webcontroler or not
// TODO indicate that there is no * in url
@Retention(RUNTIME)
@Target(METHOD)
public @interface GetMapping {

	String value () default "";
	// TODO add url so that it is more intuitive
	
	Access access () default Access.DEFAULT;
	Permission [] permissions () default {};
	BooleanEnum anyPermission () default BooleanEnum.NULL;
	
	boolean independent () default false;
	
}
