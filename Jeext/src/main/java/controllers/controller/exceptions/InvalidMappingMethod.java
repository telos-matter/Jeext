package controllers.controller.exceptions;

import java.lang.reflect.Method;

import util.exceptions.FailedRequirement;

public class InvalidMappingMethod extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	public InvalidMappingMethod (Class <?> controller, Method method, String reason) {
		super ("This mapping method '" +method +"' from this controller '" +controller+"' is invalid", reason);
	}
	
}