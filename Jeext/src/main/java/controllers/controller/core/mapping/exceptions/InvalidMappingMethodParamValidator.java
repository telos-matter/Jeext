package controllers.controller.core.mapping.exceptions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class InvalidMappingMethodParamValidator extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidMappingMethodParamValidator (Class <?> controller, Method method, Parameter parameter, String validator, String reason) {
		super ("This validator: '" +validator +"' of this parameter: '" +parameter.getName() +"' from this mapping method: '" +method.getName() +"' from this controller: '" +controller.getName() +"' is invalid\n(Reason: " +reason +")");
	}
	
}