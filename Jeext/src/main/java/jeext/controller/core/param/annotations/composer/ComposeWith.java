package jeext.controller.core.param.annotations.composer;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ComposeWith {

	String value () default "";
	
	boolean useSetter () default true;
	String setterMethod () default "";
	
}