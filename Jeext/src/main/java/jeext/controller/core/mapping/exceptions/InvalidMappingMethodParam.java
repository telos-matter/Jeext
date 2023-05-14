package jeext.controller.core.mapping.exceptions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class InvalidMappingMethodParam extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidMappingMethodParam (Class <?> controller, Method method, Parameter parameter, String reason) {
		super ("This parameter: '" +parameter.getName() +"' from this mapping method: '" +method.getName() +"' from this controller: '" +controller.getName() +"' is invalid\n(Reason: " +reason +")");
	}
	
}