package controllers.controller.exceptions;

import java.lang.reflect.Method;

import controllers.controller.core.validators.Validator;

public class InvalidParam extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidParam (String name, Validator validator) {
		super ("Param: '" +name +"' failed to be validated by: '" +validator.getClass().getName());
	}
	
}
