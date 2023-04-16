package controllers.controller.core.exceptions;

import controllers.controller.core.param.Param;
import controllers.controller.core.param.validators.Validator;

public class InvalidParameter extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private Param param;
	private Validator validator;
	
	public InvalidParameter(Param param, Validator validator) {
		this.param = param;
		this.validator = validator;
	}
	
	public InvalidParameter ( ) {
		this(null, null);
	}

	public Param getParam() {
		return param;
	}

	public Validator getValidator() {
		return validator;
	}

}
