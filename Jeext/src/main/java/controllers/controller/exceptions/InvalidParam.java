package controllers.controller.exceptions;

import controllers.controller.core.Param;
import controllers.controller.core.validators.Validator;

public class InvalidParam extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private Param param;
	private Validator validator;
	
	public InvalidParam(Param param, Validator validator) {
		this.param = param;
		this.validator = validator;
	}

	public Param getParam() {
		return param;
	}

	public Validator getValidator() {
		return validator;
	}

}
