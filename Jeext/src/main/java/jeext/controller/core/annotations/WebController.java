package jeext.controller.core.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jeext.controller.Controller;
import jeext.controller.core.Access;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.util.BooleanEnum;
import models.User;
import models.permission.Permission;

/**
 * <p>This {@link Annotation} indicates to the {@link Controller}
 * that the {@link Class} that has it is a {@link WebController}, in a similar
 * style to the {@link WebServlet} annotation
 * <p>A {@link WebController} groups a bunch of {@link WebMapping}s
 * that are common or 
 * have a common URL, and defines the general prefix
 * for their URLs (with the {@link #value()} field) for example; a {@link WebController}
 * can have the URL: `/library/` while the {@link WebMapping}s inside it will each
 * have for example `religion`, `science`, `novels`.. This will result in that that each
 * one of those {@link WebMapping}s will have a final access URL of `/library/religion`,
 * `/library/science`, and so on. Keep in mind that the user is never accessing a {@link WebController}, 
 * instead it is accessing the {@link Mapping}s identified by the {@link WebMapping} {@link Annotation}
 * inside the {@link WebController}
 * <p>Note that the final URL should start with a `/` and should NOT start
 * with `/res` or `/controller`
 * <p>A {@link WebController} can also define the default {@link Access} type
 * of its {@link WebMapping}s (with the {@link #access()} field), 
 * the default {@link Permission}s needed to
 * access its {@link WebMapping}s (with the {@link #permissions()} field), and the default value on
 * whether the {@link User} only needs
 * to have one of the required {@link Permission}s or all of them to access
 * the {@link WebMapping}s (with the {@link #anyPermission()} field)
 * <p><b>Know</b> that a {@link WebMapping} can be totally (or partially)
 * independent from the {@link WebController}
 * it resides in if it specifies that, in which case
 * all of these values are not inherited by the {@link WebMapping},
 * read more about that in {@link WebMapping#inherit()}
 * <p>{@link WebController}s who need an init method should define one
 * that is named `init` and has
 * the public and static {@link Modifier}s as well as {@link Void}
 * return type and must take no arguments. These init methods, 
 * unlike {@link HttpServlet#init()} method, are
 * all called at once when the first request is made to any of the {@link WebMapping}s.
 *
 * @see WebMapping
 * @see Access
 * @see Permission
 * @see Controller
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface WebController {
	
	/**
	 * <p>Defines the general prefix for all
	 * of this {@link WebController}'s {@link WebMapping}s' URL
	 * <p>If for example this has a value of `/library/`
	 * then the {@link WebMapping}s inside it can have
	 * an URL of `religion`, `science` and their end
	 * URL is going to be `/library/religion` and
	 * `/library/science`
	 * <p>Note that the final URL should always start
	 * with `/` and should NOT start with neither
	 * `/res` nor `/controller`
	 * <p>By default it is an empty prefix ""
	 */
	String value () default "";
	
	
	/**
	 * <p>The default {@link Access} value for
	 * all of this {@link WebController}s' {@link WebMapping}s
	 * <p>By default it is {@link Access#ALL}
	 */
	Access access () default Access.ALL;
	/**
	 * <p>The default {@link Permission} values
	 * for all of this {@link WebController}s' {@link WebMapping}s
	 * <p>Keep in mind if for example a {@link WebMapping} requires
	 * no {@link Permission}s but there are default
	 * {@link Permission}s here, then the permissions
	 * in that mapping will default to these (learn how to avoid this
	 * in {@link WebMapping#inherit()})
	 * <p>By default no {@link Permission}s are
	 * required
	 */
	Permission [] permissions () default {};
	/**
	 * <p>Specifies the default value for the {@link WebMapping}s on
	 * whether the logged in {@link User} needs to have
	 * just one of the specified {@link Permission}s
	 * or all of them
	 * <p>{@link BooleanEnum#TRUE} means any of them,
	 * {@link BooleanEnum#FALSE} means all of them.
	 * <p>{@link BooleanEnum#NULL} should be used/specified if
	 * no {@link #permissions()} is specified. Or, in the
	 * {@link WebMapping}s, to have them default to the value that is provided here
	 * <p>By default it is {@link BooleanEnum#NULL}
	 * 
	 * @see BooleanEnum
	 */
	BooleanEnum anyPermission() default BooleanEnum.NULL;
	
}
