package jeext.controller.core.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import jeext.controller.core.Access;
import jeext.controller.core.HttpMethod;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.util.BooleanEnum;
import models.permission.Permission;

/**
 * <p>This {@link Annotation} is what indicates that a {@link Method}
 * is a {@link Mapping}. And thus the terms WebMapping and Mapping
 * are used indifferently
 * <p>{@link WebMapping}s {@link Method}s
 * must reside within a {@link WebController}
 * <p>The developer indicates trough this {@link WebMapping} {@link Annotation}
 * the different values for the {@link Mapping}, that is the URL it takes
 * care of ({@link #value()}), the {@link HttpMethod} it handles ({@link #method()}),
 * what type of {@link Access} is needed ({@link #access()}), the {@link Permission}s
 * needed ({@link #permissions()}) and whether or not the user needs all of them or
 * just one of them ({@link #anyPermission()})
 * <p>Since a {@link WebController} groups a bunch of related or
 * similar {@link WebMapping}s, the values specified in the {@link WebController}
 * are inherited by the {@link WebMapping}s inside it <b>if</b> the {@link WebMapping}
 * decides to inherit from the {@link WebController} ({@link #inherit()})
 * <p>The details of how each values is used with regard to the {@link #inherit()}
 * value is specified in their respective documentation
 *
 * @see WebController
 * @see {@link Mapping}
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface WebMapping {

	/**
	 * <p>The URL that this {@link Mapping} is going to
	 * take care of
	 * <p>If this {@link WebMapping} inherits from
	 * the {@link WebController} then the value specified
	 * here is added to the prefix specified in the {@link WebController#value()}.
	 * <p>If not then the value specified here is used alone as the final URL
	 * <p>Keep in mind
	 * that the requirements of the final mappings' URL always apply, i.e.
	 * it should start with `/` and should NOT start with neither
	 * `/res` nor `/controller`.
	 * <p>Of course the final URL with the specified {@link HttpMethod}
	 * ({@link #method()}) should be unique across all of the web application
	 * <p>By default it is the empty string ""
	 */
	String value () default "";

	/**
	 * <p>The {@link HttpMethod} that this {@link Mapping}
	 * is going to accept, i.e. only request made trough/with
	 * this method are going to the {@link Mapping}
	 * <p>This value is dependent on each individual {@link Mapping}
	 * and thus obviously does not get inherited from the {@link WebController}
	 * (not that a {@link WebController} can specify one in the first place)
	 * regardless of the value of {@link #inherit()}
	 * <p>Of course the final URL ({@link #value()}) with the specified 
	 * method should be unique across all of the web application
	 * <p>By default it is {@link HttpMethod#GET}
	 */
	HttpMethod method () default HttpMethod.GET;
	
	/**
	 * <p>The {@link Access} values needed to access this {@link Mapping}
	 * <p>If this {@link WebMapping} inherits from the {@link WebController}
	 * then the value specified in {@link WebController#access()} is the
	 * one used <b>if</b> the value here is {@link Access#DEFAULT}, otherwise
	 * what ever value specified here is used as the final value
	 * of the {@link Mapping}
	 * <p>If this {@link WebMapping} does not inherit from the {@link WebController}
	 * then only the value specified here is considered and in which case
	 * the value {@link Access#DEFAULT} cannot be used
	 * <p>By default it is {@link Access#DEFAULT}
	 */
	Access access () default Access.DEFAULT;
	/**
	 * <p>The {@link Permission}s needed to access this {@link Mapping}
	 * <p>If this {@link WebMapping} inherits from the {@link WebController}
	 * then the values specified in {@link WebController#permissions()} are the
	 * ones used <b>if</b> there is no value here, otherwise
	 * what ever values specified here are used as the final value
	 * of the {@link Mapping}
	 * <p>If this {@link WebMapping} does not inherit from the {@link WebController}
	 * then only the values specified here are considered / used
	 * <p>By default no {@link Permission} is needed
	 */
	Permission [] permissions () default {};
	/**
	 * <p>Whether or not the specified {@link Permission}s ({@link #permissions()})
	 * are all
	 * needed or just one of them
	 * <p>If this {@link WebMapping} inherits from the {@link WebController}
	 * then the value specified in {@link WebController#anyPermission()} is the
	 * one used <b>if</b> the value here is {@link BooleanEnum#NULL}, otherwise
	 * what ever value specified here is used as the final value
	 * of the {@link Mapping}
	 * <p>If this {@link WebMapping} does not inherit from the {@link WebController}
	 * then only the value specified here is considered
	 * <p>If the final value of {@link Permission}s of the {@link Mapping}
	 * is not empty (i.e. there are some {@link Permission} needed) then
	 * the final value of anyPermission cannot be {@link BooleanEnum#NULL}.
	 * If not, if its empty, then the final value of anyPermission must
	 * be {@link BooleanEnum#NULL}
	 * <p>By default it is {@link BooleanEnum#NULL}
	 */
	BooleanEnum anyPermission () default BooleanEnum.NULL;
	
	/**
	 * <p>If set to <code>true</code> then
	 * this {@link WebMapping} will inherit the
	 * values specified in the {@link WebController} it
	 * resides in <b>if</b> it does not specify any (the meaning of not specifying
	 * any value depends on the member it self, for example
	 * in the case of {@link #access()} that would be {@link Access#DEFAULT},
	 * and in {@link #anyPermission()} that would be {@link BooleanEnum#NULL}
	 * , and so on..)
	 * <p>If set to <code>false</code> then the values
	 * in the {@link WebController} are totally dismissed and
	 * only the ones used here are taken into account
	 * <p>Read the documentation of the individual members to learn more about how
	 * the value specified here affects how they (the values specified in
	 * the members) are interpreted/used
	 * <p>By default it is <code>true</code>
	 */
	boolean inherit () default true;
	
}
