package jeext.controller.core.exceptions;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.Validator;

// TODO document
// Not runtimeexcpetion intentionally
public class InvalidParameter extends Exception {
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
