package controllers.controller.exceptions;

import java.lang.reflect.Method;

public class InvalidMappingMethod extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidMappingMethod (Class <?> controller, Method method, String reason) {
		super ("This mapping method: '" +method.getName() +"' from this controller: '" +controller.getName() +"' is invalid\n(Reason: " +reason +")");
	}
	
}