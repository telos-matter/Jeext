package jeext.controller.core.param.composer.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Composed {
	
	String value () default "";
	
	boolean retrieveFirst () default false; // db
	// what about null in id, when should it be called with default n what not
	// auto ignore on serializable
	
}
