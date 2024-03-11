package jeext.controller.core.mapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.Controller;
import jeext.controller.core.Access;
import jeext.controller.core.HTTPMethod;
import jeext.controller.core.IdentifiableUser;
import jeext.controller.core.Path;
import jeext.controller.core.annotations.WebController;
import jeext.controller.core.annotations.WebMapping;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethod;
import jeext.controller.core.param.Param;
import jeext.util.exceptions.UnhandledJeextException;
import jeext.util.exceptions.UnsupportedType;
import models.User;
import models.permission.Permission;

/**
 * <p>A {@link Mapping} represents a way for an HTTP request
 * to access a specific resource or do some sort of treatment,
 * it is the equivalent of the {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}
 * and {@link HttpServlet#doPost(HttpServletRequest, HttpServletResponse)} methods
 * but for a specific URL (and a specific HTTP method) instead of a pattern of URLs
 * <p>A {@link Mapping} is <i>nothing more</i> than a 
 * public, static, void returning, {@link WebMapping} annotated {@link Method} that
 * resides in a {@link Class} annotated by the {@link WebController} annotation. And
 * because in order for a {@link Method} to be marked as a {@link Mapping}
 * it should be annotated with the {@link WebMapping} {@link Annotation}, the terms
 * WebMapping and Mapping are used indifferently because they represent the
 * same thing more or less
 * <p>A {@link Mapping} {@link Method} should have its last two parameters be
 * of the type {@link HttpServletRequest} and {@link HttpServletResponse} respectively
 * <p>A {@link Mapping} {@link Method} can also have parameters that it is expecting
 * from the user's request as parameters of that method,
 * read more about that in {@link Param}
 * <p><b>Conception note:</b> a {@link Mapping} has no information about the actual
 * {@link Path} it handles nor about the {@link HTTPMethod} it takes care of. That
 * job is left to the {@link Controller} and {@link MappingCollection}
 * 
 * @see Param
 * @see WebMapping
 * @see WebController
 * @see #invoke(HttpServletRequest, HttpServletResponse)
 */
public class Mapping {

	/**
	 * The underlying {@link Method} that is this {@link Mapping}
	 */
	private Method method;
	/**
	 * An array of {@link Parameter}s ({@link Param}s) that this {@link Mapping}
	 * {@link Method} has. This of course does not include the
	 * {@link HttpServletRequest} and {@link HttpServletResponse} {@link Parameter}s
	 */
	private Param [] params;
	/**
	 * The {@link Access} value of this {@link Mapping} {@link Method}
	 */
	private Access access;
	/**
	 * The {@link Permission}s needed to access this {@link Mapping} {@link Method}
	 */
	private Permission [] permissions;
	/**
	 * Specifies if user requires all the {@link #permissions} specified
	 * or just one of them, or <code>null</code> if there are no {@link #permissions}
	 */
	private Boolean anyPermission;
	
	/**
	 * The constructor that validates and creates {@link Mapping}s
	 * along side their {@link Param}s
	 * 
	 * @throws InvalidMappingMethod	If one of the requirements of a {@link Mapping} are not met
	 */
	public Mapping (Class <?> webController, Method method, Access access, Permission[] permissions, Boolean anyPermission) {
		int modifiers = method.getModifiers();
		if ((! Modifier.isPublic(modifiers)) ||
				(! Modifier.isStatic(modifiers)) ||
				(! void.class.equals(method.getReturnType()))) {
   			throw new InvalidMappingMethod(webController, method, "Mappings should be public, static and have a return type of void");
   		}
		
		Parameter [] parameters = method.getParameters();
		
		if ((parameters.length < 2) ||
				(parameters[parameters.length -2].getType() != HttpServletRequest.class) ||
				(parameters[parameters.length -1].getType() != HttpServletResponse.class)) {
			throw new InvalidMappingMethod(webController, method, "Mappings should have the HttpServletRequest and HttpServletResponse parameters as their last two parameters respectively.");
		}
		
		this.method = method;
		
		this.params = new Param [parameters.length -2];
		for (int i = 0; i < this.params.length; i++) {
			this.params[i] = new Param(webController, method, parameters[i]);
		}
		
		if (access == Access.DEFAULT) {
			throw new InvalidMappingMethod(webController, method, "Access type can't be `default` in the webController if the webMapping inherits from it, or in the webMapping if it does not inherit form the webController.");
		}
		this.access = access;
		
		if (anyPermission == null && permissions.length != 0) {
			throw new InvalidMappingMethod(webController, method, "Must specify an anyPermission value since permissions are set.");
		}
		
		if (anyPermission != null && permissions.length == 0) {
			throw new InvalidMappingMethod(webController, method, "The value in anyPermission should be `null` since there are no permissions.");
		}
		
		this.permissions = permissions;
		this.anyPermission = anyPermission;
   	}
	
	/**
	 * <p>The method that prepares the {@link Param}s and calls
	 * the underlying {@link Method}, i.e. this {@link Mapping}
	 * for it respond to the HttpServletRequest
	 * <p>The {@link Exception}s this method throw are left to be
	 * handled accordingly by the {@link Controller}, which is the
	 * only place from which this method is called
	 * 
	 * @throws InvalidParameter	if one of the {@link Param}s throws one to indicate that the request's parameters do not fulfill the requirements
	 * @throws Exception	if the underlying {@link Method} throws any {@link Exception}, or any of the {@link Method}s used in the {@link Param}s
	 */
	// TODO update doc for throws
	public void invoke (HttpServletRequest request, HttpServletResponse response) throws InvalidParameter, InvocationTargetException {
		Object [] parameters = new Object [params.length +2];
		
		parameters[parameters.length -2] = request;
		parameters[parameters.length -1] = response;
		
		for (int i = 0; i < params.length; i++) {
			parameters[i] = params[i].getParam(request);
		}
		
		try {
			
			method.invoke(null, parameters);
			
		} catch (IllegalAccessException | IllegalArgumentException  e) {
			throw new UnhandledJeextException(e);
		} 
	}
	
	/**
	 * @return	whether or not the {@link IdentifiableUser}
	 * can access this {@link Mapping} depending
	 * on the {@link Access} value specified for
	 * this {@link Mapping}
	 */
	public boolean canAccess (IdentifiableUser user) {
		switch (access) {
		
		case ALL: return true;
		case IDENTIFIED: return user != null;
		case ANONYMOUS: return user == null;
		case NONE: return false;
		
		default: throw new UnsupportedType(access);
		}
	}
	
	/**
	 * @return	whether or not any {@link Permission}s
	 * are needed to access this {@link Mapping}
	 */
	public boolean needsPermission () {
		return permissions.length != 0;
	}
	
	/**
	 * @return	whether or not the passed {@link Permission}s
	 * (those of a {@link User}) allow for access to this {@link Mapping}
	 * , with regards
	 * to the {@link Permission}s needed
	 * and the {@link #anyPermission} value
	 * 
	 * @throws NullPointerException	if the passed {@link Collection} is <code>null</code>
	 */
	public boolean hasPermission (Collection <Permission> permissions) {
		Objects.requireNonNull(permissions);
		
		if (this.permissions.length == 0) {
			return true;
			
		} else if (permissions.size() == 0) {
			return false;
			
		} else if (this.anyPermission) {
			for (Permission permission : this.permissions) {
				if (permissions.contains(permission)) {
					return true;
				}
			}
			
			return false;
		} else {
			for (Permission permission : this.permissions) {
				if (!permissions.contains(permission)) {
					return false;
				}
			}
			
			return true;
		}
	}

	@Override
	public String toString() {
		return "" +method;
	}
	
}
