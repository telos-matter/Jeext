package jeext.controller.core;

import java.util.Collection;

import jeext.model.Model;
import models.permission.Permission;

/**
 * <p>The interface that the {@link Model} class who
 * defines the user who is going to login should implement.
 */
public interface IdentifiableUser {

	public abstract Collection<Permission> getPermissions ();
	
}
