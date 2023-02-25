package models.core;

import controllers.controller.Controller;

/**
 * <p>List of permissions available
 * <p>Used in {@link Controller}s to determine
 * what permissions are needed to access a web page or resource
 * <p>Add and remove permissions as you please
 */
public enum Permission {
	
	ROOT,
	ADMIN,
	CASUAL;
	
}
