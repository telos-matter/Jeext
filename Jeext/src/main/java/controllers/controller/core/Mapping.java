package controllers.controller.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import controllers.controller.exceptions.InvalidMappingMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Mapping {

	private Method method;
	private Param [] params;
	
	
	
	public Mapping (Class <?> controller, Method method) {
		if ((! Modifier.isPublic(method.getModifiers())) ||
				(! Modifier.isStatic(method.getModifiers())) ||
				(! Void.class.equals(method.getReturnType()))) {
   			throw new InvalidMappingMethod(controller, method, "Mappings should have the public and static modifiers, and a return type of void");
   		}
		
		Parameter [] parameters = method.getParameters();
		
		if ((parameters.length < 2) ||
				(parameters[parameters.length -2].getType() != HttpServletRequest.class) ||
				(parameters[parameters.length -1].getType() != HttpServletResponse.class)) {
			throw new InvalidMappingMethod(controller, method, "Mappings should have the HttpServlet-Request/Response parameters as their last two parameters");
		}
		
		this.method = method;
		
		this.params = new Param [parameters.length -2];
		for (int i = 0; i < this.params.length -2; i++) {
			this.params[i] = new Param(controller, method, parameters[i]);
		}
   	}
	
	public void invoke (HttpServletRequest request, HttpServletResponse response) {
		Object [] parameters = new Object [params.length +2];
		
		parameters[parameters.length -2] = request;
		parameters[parameters.length -1] = response;
		
		for (int i = 0; i < parameters.length -2; i++) {
			parameters[i] = params[i].getParam(request);
		}
		
		try {
			method.invoke(null, parameters);
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		}
	}
	
}
