package jeext.controller.core.param.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// MENTION the compiler option
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Name {

	String value();
	
}
