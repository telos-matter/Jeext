package controllers.controller.exceptions;

import java.lang.reflect.Method;

public class InvalidMappingMethod extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidMappingMethod (Class <?> clazz, Method method) {
		super ("This mapping method: \"" +method.getName() +"\" from this controller: \"" +clazz.getName() +"\" is invalid");
	}
	
}