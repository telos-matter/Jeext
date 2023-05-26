package jeext.controller.core.param.validators;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.validators.annotations.Required;

/**
 * <p>The implementation of the {@link Required} {@link Validator}
 * 
 * @see Required
 * @see Param
 */
public class RequiredValidator implements Validator {

	private static final RequiredValidator VALIDATOR = new RequiredValidator();
	
	public static RequiredValidator GET () {
		return VALIDATOR;
	}
	
	private RequiredValidator () {}
	
	@Override
	public boolean validate (Object object) {
		return object != null;
	}

}
