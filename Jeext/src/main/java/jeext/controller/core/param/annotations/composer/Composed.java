package jeext.controller.core.param.annotations.composer;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Composed {
	// CONSIDER if the name annotation is used, should it override the id field name regardless of retrievefirst?
	boolean ignoreID () default false; // does not override ignore if exists on the field
	
	boolean requireAll () default true; // all fields are required, if they are not here then invalid 
	
	boolean retrieveFirst () default false; // db
	// what about null in id, when should it be called with default n what not
	// auto ignore on serializable
	// all fields should be here or not
	
}
