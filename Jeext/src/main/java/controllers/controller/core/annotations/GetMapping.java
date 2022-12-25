package controllers.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import controllers.controller.core.Access;
import models.core.Permission;

@Retention(RUNTIME)
@Target(METHOD)
public @interface GetMapping {

	String value () default "";
	
	Access access () default Access.DEFAULT;
	Permission [] permission () default {};
	
}
