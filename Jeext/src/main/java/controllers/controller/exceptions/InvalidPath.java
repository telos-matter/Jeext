package controllers.controller.exceptions;

import java.lang.reflect.Method;

public class InvalidPath extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidPath (Class <?> controller, Method method, String path) {
		super ("This path: '" +path +"' of this mapping method: '" +method.getName() +"' from this controller: '" +controller.getName() +"' is invalid");
	}
	
}
