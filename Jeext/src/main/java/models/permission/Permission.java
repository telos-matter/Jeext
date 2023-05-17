package models.permission;

import jakarta.servlet.http.HttpSession;
import jeext.controller.core.Access;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import models.User;

/**
 * <p>List of permissions available
 * <p>Used in {@link WebController}s and {@link WebMapping}s
 * to determine
 * what permissions are needed,
 * by the logged in {@link User}, to access a web page or resource
 * <p>If no user is logged in the {@link HttpSession} and a
 * permission (or multiple) is (are) needed then they
 * are denied access
 * <p>If no permissions is needed to access a web page or resource, 
 * but just rather a
 * {@link User} must be logged in, use {@link Access#IDENTIFIED}
 * <p>Add and remove permissions as you please
 * 
 * @see Access
 * @see WebController
 * @see WebMapping
 */
public enum Permission {
	
	ROOT,
	ADMIN,
	CASUAL;
	
}
