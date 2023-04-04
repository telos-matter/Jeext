package controllers.controller.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

import controllers.controller.Controller;
import controllers.controller.core.Access;
import controllers.controller.core.util.BooleanEnum;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import models.User;
import models.core.Permission;

/**
 * <p>This {@link Annotation} indicates to the {@link Controller}
 * that the {@link Class} that has it is a {@link WebController}, in a similar
 * style to the {@link WebServlet} annotation
 * <p>{@link WebController}s should be in the {@link controllers} package, that is
 * where the {@link Controller} will look for them to include them and manage them (
 * read more in the {@link Controller#load(jakarta.servlet.ServletContext)} method
 * to learn how to add or specify <i>external</i> {@link WebController}s)
 * <p>A {@link WebController} groups a bunch of {@link GetMapping}s and
 * {@link PostMapping}s that are common or 
 * have a common URL, and defines the general prefix
 * for their URLs (with the {@link #value()} field) for example; a {@link WebController}
 * can have the URL: "/library/" while the {@link GetMapping}s inside it will each
 * have for example "religion", "science", "novels".. This will result in that that each
 * one of those {@link GetMapping}s will have a final access URL of "/library/religion",
 * "/library/science"..
 * <p>Note that the final URL should start with a "/" and should NOT start
 * with "/res" or "/controllers"
 * <p>A {@link WebController} can also define the default {@link Access} type
 * of its mappings (with the {@link #access()} field), 
 * the default {@link Permission}s needed to
 * access its mappings (with the {@link #permissions()} field), and the default value on
 * whether the {@link User} only needs
 * to have one of the required {@link Permission}s or all of them to access
 * the mapping (with the {@link #anyPermission()} field)
 * <p>{@link WebController}s who need an init method should define one
 * that is named 'init' and has
 * the public and static {@link Modifier}s as well a {@link Void}
 * return type and must take no arguments. These init methods, 
 * unlike {@link HttpServlet#init()} method, are
 * all called at once when the first request is made to any of the {@link WebController}s.
 * 
 * @see GetMapping
 * @see PostMapping
 * @see Access
 * @see Permission
 * @see Controller
 * 
 * @author telos_matter
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface WebController {
	
	/**
	 * <p>Defines the general prefix for all
	 * of this {@link WebController}s' mappings' URL
	 * <p>If for example this has a value of "/library/"
	 * then the mappings inside it can have
	 * an URL of "religion", "science" and their end
	 * URL is going to be "/library/religion" and
	 * "/library/science"
	 * <p>Note that the final URL should always start
	 * with "/" and should NOT start with neither
	 * "/res" nor "/controllers"
	 * <p>By default it is an empty prefix ""
	 */
	String value () default "";
	
	
	/**
	 * <p>The default {@link Access} value for
	 * all of this {@link WebController}s' mappings
	 * <p>By default it is {@link Access#ALL}
	 */
	Access access () default Access.ALL;
	/**
	 * <p>The default {@link Permission} values
	 * for all of this {@link WebController}s' mappings
	 * <p>Keep in mind if for example a mapping requires
	 * no {@link Permission}s but there are default
	 * {@link Permission}s here, then the permissions
	 * in that mapping will default to these
	 * <p>By default no {@link Permission}s are
	 * required
	 */
	Permission [] permissions () default {};
	/**
	 * <p>Specifies the default value for the mappings on
	 * whether the logged in {@link User} needs to have
	 * just one of the specified {@link Permission}s
	 * or all of them
	 * <p>{@link BooleanEnum#TRUE} means any of them,
	 * {@link BooleanEnum#FALSE} means all of them.
	 * <p>{@link BooleanEnum#NULL} can only be used in the
	 * mappings to have them default to the value that is specified here,
	 * or in case no {@link Permission} is specified this can
	 * take on any value
	 * <p>By default it is {@link BooleanEnum#TRUE}
	 * 
	 * @see BooleanEnum
	 */
	BooleanEnum anyPermission() default BooleanEnum.TRUE;
	
}
