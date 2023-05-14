package jeext.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.Access;
import jeext.controller.core.HttpMethod;
import jeext.controller.core.util.BooleanEnum;
import jeext.models_core.Permission;
import models.User;

@Retention(RUNTIME)
@Target(METHOD)
public @interface WebMapping {

	String value () default "";

	HttpMethod method () default HttpMethod.GET;
	
	Access access () default Access.DEFAULT;
	Permission [] permissions () default {};
	BooleanEnum anyPermission () default BooleanEnum.NULL;
	
	boolean inherit () default true;
	
}
