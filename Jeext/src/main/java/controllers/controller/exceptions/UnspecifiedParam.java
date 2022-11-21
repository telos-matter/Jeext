package controllers.controller.exceptions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class UnspecifiedParam extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnspecifiedParam (Class <?> clazz, Method method, Parameter param) {
		super ("This parameter: \"" +param.getName() +"\" of type" +param.getType().getName() +" from this method: \"" +method.getName() +"\" from this controller: \"" +clazz.getName() +"\" is unspecified");
	}
	
}