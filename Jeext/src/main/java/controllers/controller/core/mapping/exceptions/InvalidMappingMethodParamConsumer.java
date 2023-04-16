package controllers.controller.core.mapping.exceptions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class InvalidMappingMethodParamConsumer extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidMappingMethodParamConsumer (Class <?> controller, Method method, Parameter parameter, String consumer, String reason) {
		super ("This consumer: '" +consumer +"' of this parameter: '" +parameter.getName() +"' from this mapping method: '" +method.getName() +"' from this controller: '" +controller.getName() +"' is invalid\n(Reason: " +reason +")");
	}
	
}