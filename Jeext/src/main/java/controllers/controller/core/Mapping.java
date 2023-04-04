package controllers.controller.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Objects;

import controllers.controller.exceptions.InvalidMappingMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;
import models.core.Permission;
import util.exceptions.UnhandledException;
import util.exceptions.UnsupportedType;

public class Mapping {

	private Method method;
	private Param [] params;
	private Access access;
	private Permission [] permissions;
	private Boolean anyPermission;
	
	public Mapping (Class <?> controller, Method method, Access access, Permission[] permissions, Boolean anyPermission) {
		int modifiers = method.getModifiers();
		if ((! Modifier.isPublic(modifiers)) ||
				(! Modifier.isStatic(modifiers)) ||
				(! void.class.equals(method.getReturnType()))) {
   			throw new InvalidMappingMethod(controller, method, "Mappings should have the public and static modifiers, and a return type of void");
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
			throw new InvalidMappingMethod(controller, method, "Access type can't be default in controllers!");
		}
		this.access = access;
		
		if (anyPermission == null && permissions.length != 0) {
			throw new InvalidMappingMethod(controller, method, "Must specify an anyPermission value since permissions are set");
		}
		this.permissions = permissions;
		this.anyPermission = anyPermission;
   	}
	
	public void invoke (HttpServletRequest request, HttpServletResponse response) throws Throwable {
		Object [] parameters = new Object [params.length +2];
		
		parameters[parameters.length -2] = request;
		parameters[parameters.length -1] = response;
		
		for (int i = 0; i < params.length; i++) {
			parameters[i] = params[i].getParam(request);
		}
		
		try {
			method.invoke(null, parameters);
			
		} catch (InvocationTargetException exception) {
			throw exception.getCause();
					
		} catch (IllegalAccessException | IllegalArgumentException  e) {
			throw new UnhandledException(e);
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
