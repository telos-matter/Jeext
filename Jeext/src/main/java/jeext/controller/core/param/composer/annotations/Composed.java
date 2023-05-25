package jeext.controller.core.param.composer.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Composed {
	
	String value () default "";
	
	boolean requireAll () default true; // all fields are required, if they are not here then invalid 
	
	boolean retrieveFirst () default false; // db
	// what about null in id, when should it be called with default n what not
	// auto ignore on serializable
	// all fields should be here or not
	
}
