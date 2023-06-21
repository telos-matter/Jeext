package jeext.controller.core.param.annotations.composer;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Composed {
	// MENTION the param name is that by which the id will be looked for, regardless of its composeWith
	
	boolean ignoreID () default false; // false wont override ignore if exists on the field
	
	boolean requireAll () default true; // all fields are required, if they are not here then invalid 
	
	boolean retrieveFirst () default false; // db
	
}
