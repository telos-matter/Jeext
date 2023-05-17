package jeext.controller.core;

import jakarta.servlet.http.HttpSession;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import models.User;
import models.permission.Permission;

/**
 * <p>An {@link Enum} to specify who can
 * access a web page or resource 
 * <p>This is different than {@link Permission}s
 * in that the specifications of who can access is
 * based on their identification, are they logged in or not
 * <p>Used in {@link WebController}s and {@link WebMapping}s
 * 
 * @see Permission
 * @see WebController
 * @see WebMapping
 */
public enum Access {

	/**
	 * Should never be used in {@link WebController}s.
	 * It is the
	 * default value of {@link WebMapping}s
	 * to indicate that the access right to this web page or resource
	 * is determined by the {@link WebController} they belong to, in case
	 * the {@link WebMapping} does inherit from the {@link WebController},
	 * if that is not the case (if the {@link WebMapping} does not inherit from the
	 * {@link WebController}) then this value cannot be used in
	 * the {@link WebMapping}
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
