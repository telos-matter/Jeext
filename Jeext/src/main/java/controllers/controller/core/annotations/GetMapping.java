package controllers.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import controllers.controller.core.Access;
import controllers.controller.core.util.BooleanEnum;
import models.User;
import models.core.Permission;

// TODO should add and specify that anyPermission should be null if there is not permissions?
// TODO add an independent variable to indicate whether it should inherite from the webcontroler or not
@Retention(RUNTIME)
@Target(METHOD)
public @interface GetMapping {

	String value () default "";
	
	Access access () default Access.DEFAULT;
	Permission [] permissions () default {};
	BooleanEnum anyPermission () default BooleanEnum.NULL;
	
	boolean independent () default false;
	
}
