package controllers.controller.exceptions;

import java.lang.reflect.Method;

public class PathDuplicate extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PathDuplicate (Class <?> controller, Method method, String path) {
		super ("This path: '" +path +"' of this mapping method: '" +method.getName() +"' from this controller: '" +controller.getName() +"' is already taken");
	}
	
}
