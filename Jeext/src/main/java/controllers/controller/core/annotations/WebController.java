package controllers.controller.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import controllers.controller.core.Access;
import models.core.Permission;

@Retention(RUNTIME)
@Target(TYPE)
public @interface WebController {
	
	String value () default "";
	
	Access access () default Access.ALL;
	Permission [] permission () default {};
	
}
