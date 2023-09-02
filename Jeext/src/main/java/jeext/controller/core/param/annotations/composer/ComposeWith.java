package jeext.controller.core.param.annotations.composer;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jeext.controller.core.param.Composer;
import jeext.controller.core.param.Param;
import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.Name;
import jeext.model.Model;

/**
 * <p>This {@link Annotation} indicates that how a {@link Field}
 * in a {@link Model} should
 * be retrieved and filled.
 * <p>Know that this annotation is not necessary
 * because the {@link Composer} tries to retrieve all the fields 
 * (except those marked {@link Ignore} of course) it can
 * retrieve. Read more about how it tries to retrieve them
 * in the elements of this annotation
 * <p>Can only be used on {@link Field}s whose type is supported by {@link Param}
 * <p>Obviously this annotation is mutually exclusive with {@link Ignore}
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface ComposeWith {

	/**
	 * <p>The name of the parameter from the incoming HTTP request
	 * that contains the
	 * value of this {@link Field}
	 * <p>By default, if left as
	 * a blank {@link String} or the {@link Annotation}
	 * is not used at all, it is the name of the {@link Field}
	 * <p>In the case of the ID {@link Field} (the one
	 * that you should annotate with {@link MID}), this element
	 * {@link #value()} has no effect, as that it take
	 * the name of the {@link Model} {@link Param} or
	 * the value of the {@link Name} {@link Annotation} if used
	 */
	String value () default "";
	
	/**
	 * <p>Whether or not to use 
	 * the setter method to set the value
	 * of the {@link Field}
	 * <p>The setter {@link Method} should be
	 * public, non-static, void returning and
	 * takes a single {@link Parameter} that
	 * must be the same type as the {@link Field}
	 * <p>If the {@link Field} is anything other than public then
	 * you are obligated to use a setter method
	 * <p>The setter method <i>should</i> not throw any
	 * {@link Exception}s as that {@link jeext} is just
	 * going to throw them back
	 * <p>By default it is <code>true</code>, and in the case
	 * the annotation is not used at all the {@link Composer}
	 * looks for the appropriate setter method
	 * regardless of the visibility of the {@link Field}
	 * (public, private..), if it does not find one
	 * and the {@link Field} is anything other than public
	 * it just skips that {@link Field}
	 */
	boolean useSetter () default true;
	/**
	 * <p>The name of the setter method that is going to be used
	 * to set the value of this field
	 * <p>By default, if left as a
	 * blank {@link String} or the annotation 
	 * is not used at all, it is the method with the name
	 * `setFoo` where `foo` is the {@link Field}s' name
	 */
	String setterMethod () default "";
	
}
