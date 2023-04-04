package controllers.controller.core;

import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.WebController;
import jakarta.servlet.http.HttpSession;
import models.User;
import models.core.Permission;

/**
 * <p>An {@link Enum} to specify who can
 * access a web page or resource 
 * <p>This is different than {@link Permission}s
 * in that the specifications of who can access is
 * based on their identification, are they logged in or not
 * <p>Used in {@link WebController}s, {@link GetMapping}s and
 * {@link PostMapping}s
 * @see Permission
 * @see WebController
 * @see GetMapping
 * @see PostMapping
 */
public enum Access {

	/**
	 * Should not be used in {@link WebController}s. It is the
	 * default value of {@link GetMapping}s and {@link PostMapping}s
	 * to indicate that the access right to this web page or resource
	 * is determined by the {@link WebController} they belong to
	 */
	DEFAULT,
	
	/**
	 * No one can access the web page or resource. Good when you
	 * want to temporarily block access to a web page or resource
	 */
	NONE,
	/**
	 * Anyone can access the web page or resource, logged in or not
	 */
	ALL,
	/**
	 * Only logged in {@link User}s can access this web page or resource.
	 * Know that a {@link User} is considered logged in only if his {@link User}
	 * {@link Object} is an attribute in the {@link HttpSession} under the name
	 * "user"
	 */
	IDENTIFIED,
	/**
	 * Only non-logged in {@link User}s can access this web page or resource. For
	 * example; your log-in page, you wouldn't want logged in {@link User}s to
	 * go there if they are already logged in
	 */
	ANONYMOUS;
	
}
