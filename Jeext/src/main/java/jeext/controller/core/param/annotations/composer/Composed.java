package jeext.controller.core.param.annotations.composer;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.param.Composer;
import jeext.controller.core.param.Param;
import jeext.controller.core.param.consumers.Consumer;
import jeext.controller.core.param.consumers.annotations.Default;
import jeext.controller.core.param.validators.Validator;
import jeext.controller.core.param.validators.annotations.Required;
import jeext.model.Model;

/**
 * <p>This {@link Annotation}, used only on {@link Model} type of {@link Param}s,
 * indicates that that {@link Model} should be composed from the incoming
 * HTTP request. That is, that its {@link Field}s are going to filled
 * with the values from the HTTP request parameters
 * <p>For example if a {@link Model} has a {@link String} {@link Field}
 * called `name` then this {@link Annotation} will look for a parameter
 * called `name` in the incoming HTTP request and put its value in the {@link Field}
 * <p>The {@link Composed} {@link Annotation} is usually used in conjunction
 * with the {@link ComposeWith} and {@link Ignore} {@link Annotation}
 * on every {@link Field}
 * to indicate how to retrieve it. By default
 * (if {@link ComposeWith} and {@link Ignore} are not used
 * on a specific {@link Field})
 * the {@link Composer} (the class that actually implements
 * the functionalities of this {@link Annotation})
 * tries to add it
 * to a list of {@link Field}s that it is going
 * to retrieve from every incoming HTTP request
 * (you can read more about how it is going to retrieve
 * these fields that are not marked with the {@link ComposeWith}
 * {@link Annotation} in the {@link ComposeWith} {@link Annotation}
 * documentation)
 * but if it fails to add it to this list
 * (maybe because it is of an unsupported type, or it
 * is private but there is no suitable setter {@link Method}..)
 * it just skips over it and does not signal it. If
 * this is not the behavior you want, then use the
 * {@link ComposeWith} {@link Annotation} on every {@link Field}
 * that you want retrieved and {@link Ignore} on every
 * {@link Field} you want to be ignored, that way,
 * if it fails to add a {@link Field} to its list
 * it will signal it by throwing an {@link Exception}
 * 
 * <p>At the moment, this {@link Annotation} cannot be used with any other
 * {@link Param} {@link Annotation}s, like the {@link Validator}s
 * or {@link Consumer}s (and also because there isn't an actual practical way
 * to use them with it to validate and consume {@link Field}s), but a way
 * to still be able to validate the {@link Field} is to have
 * additional unused {@link Param}s in the {@link Mapping} {@link Method}
 * that are the same type and name (the name by which it is
 * going to be retrieved) as that of the {@link Field}, and to put
 * any {@link Validator} that you want on them
 *  
 * <p><b>Know that</b> {@link Composed} is mutually exclusive with
 * {@link Required} and {@link Default}
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Composed {

	/**
	 * <p>Whether or not to ignore the ID
	 * {@link Field} and not retrieve it
	 * <p>If the ID {@link Field} is
	 * annotated with {@link Ignore} then
	 * a value of <code>false</code> here
	 * won't override it
	 * <p>By default <code>false</code>
	 */
	boolean ignoreID () default false;
	
	/**
	 * <p>Whether all the {@link Field}s (those
	 * that are going to be retrieved) are required
	 * or not. If any of them is missing from the HTTP
	 * request or cannot be <i>casted</i> to the appropriate type
	 * then an {@link InvalidParameter} {@link Exception} is thrown
	 * <p>By default <code>true</code>
	 */
	boolean requireAll () default true;
	
	/**
	 * <p>Whether or not to first retrieve the {@link Model}
	 * from the DB and then fill in the {@link Field}
	 * <p>Useful when for example you want to update a {@link Model}
	 * <p>The method used to retrieve the {@link Model} is
	 * the find method of the {@link Model}
	 * <p>By default <code>false</code>
	 */
	boolean retrieveFirst () default false;
	
}
