package controllers.controller.core.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;

import controllers.controller.core.Access;
import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.WebController;
import controllers.controller.core.exceptions.InvalidParameter;
import controllers.controller.core.mapping.exceptions.InvalidMappingMethod;
import controllers.controller.core.param.Param;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import models.core.Permission;
import util.exceptions.UnhandledDevException;
import util.exceptions.UnsupportedType;

/**
 * <p>A {@link Mapping} represents a way for an HTTP request
 * to access a specific resource or do some sort of treatment,
 * it is the equivalent of the {@link HttpServlet#doGet(HttpServletRequest, HttpServletResponse)}
 * and {@link HttpServlet#doPost(HttpServletRequest, HttpServletResponse)} methods
 * but for a specific URLs instead of a pattern of URLs
 * <p>A {@link Mapping} is nothing more than a 
 * public, static, void returning method that
 * resides in a {@link Class} annotated by the {@link WebController} annotation
 * which in turn (the {@link Class}) is in the {@link controllers} package. The {@link Mapping}
 * method should specify its type, and the fact that it is indeed
 * a {@link Mapping}, by being annotated by the
 * {@link GetMapping} or {@link PostMapping} annotation. And it is that
 * {@link Method} that gets invoked when an request is made to its {@link Mapping}
 * <p>A {@link Mapping} can define its own {@link Access} value and
 * {@link Permission}s needed to access it as well as whether all of
 * the permissions are required or only one of them. Or it can inherit the
 * values specified by its {@link WebController}
 * <p>A {@link Mapping} method should have its last two parameters be
 * of the type {@link HttpServletRequest} and {@link HttpServletResponse} respectively
 * <p>A {@link Mapping} method can also have parameters that it is expecting
 * from the user as parameters of that method, read more about that in {@link Param}
 * 
 * @see WebController
 * @see Param
 * @see GetMapping
 * @see PostMapping
 * 
 * @see #invoke(HttpServletRequest, HttpServletResponse)
 */
public class Mapping {

	/**
	 * The underlying {@link Method} that is this {@link Mapping}
	 */
	private Method method;
	/**
	 * An array of parameters ({@link Param}s) that this {@link Mapping}
	 * {@link Method} has. This of course does not include the
	 * {@link HttpServletRequest} and {@link HttpServletResponse} parameters
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
	 * @param controller
	 * @param method
	 * @param access
	 * @param permissions
	 * @param anyPermission
	 */
	public Mapping (Class <?> controller, Method method, Access access, Permission[] permissions, Boolean anyPermission) {
		int modifiers = method.getModifiers();
		if ((! Modifier.isPublic(modifiers)) ||
				(! Modifier.isStatic(modifiers)) ||
				(! void.class.equals(method.getReturnType()))) {
   			throw new InvalidMappingMethod(controller, method, "Mappings should be public, static and have a return type of void");
   		}
		
		Parameter [] parameters = method.getParameters();
		
		if ((parameters.length < 2) ||
				(parameters[parameters.length -2].getType() != HttpServletRequest.class) ||
				(parameters[parameters.length -1].getType() != HttpServletResponse.class)) {
			throw new InvalidMappingMethod(controller, method, "Mappings should have the HttpServletRequest and HttpServletResponse parameters as their last two parameters respectively");
		}
		
		this.method = method;
		
		this.params = new Param [parameters.length -2];
		for (int i = 0; i < this.params.length; i++) {
			this.params[i] = new Param(controller, method, parameters[i]);
		}
		
		if (access == Access.DEFAULT) {
			throw new InvalidMappingMethod(controller, method, "Access type can't be `default` in controllers!");
		}
		this.access = access;
		
		if (anyPermission == null && permissions.length != 0) {
			throw new InvalidMappingMethod(controller, method, "Must specify an anyPermission value since permissions are set");
		}
		this.permissions = permissions;
		this.anyPermission = anyPermission;
   	}
	
	public void invoke (HttpServletRequest request, HttpServletResponse response) throws InvalidParameter, Throwable {
		Object [] parameters = new Object [params.length +2];
		
		parameters[parameters.length -2] = request;
		parameters[parameters.length -1] = response;
		
		for (int i = 0; i < params.length; i++) {
			parameters[i] = params[i].getParam(request);
		}
		
		try {
			
			method.invoke(null, parameters);
			
		} catch (IllegalAccessException | IllegalArgumentException  e) {
			throw new UnhandledDevException(e);
		} 
	}
	
	public boolean canAccess (User user) {
		switch (access) {
		
		case ALL: return true;
		case IDENTIFIED: return user != null;
		case ANONYMOUS: return user == null;
		case NONE: return false;
		
		default: throw new UnsupportedType(access);
		}
	}
	
	public boolean needsPermission () {
		return permissions.length != 0;
	}
	
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
	
}
