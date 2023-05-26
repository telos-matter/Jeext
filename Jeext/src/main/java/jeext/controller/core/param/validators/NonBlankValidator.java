package jeext.controller.core.param.validators;

import jeext.controller.core.param.validators.annotations.NonBlank;

/**
 * The implementation of the {@link NonBlank} {@link Validator}
 */
public class NonBlankValidator implements Validator {

	private static final NonBlankValidator VALIDATOR = new NonBlankValidator();
	
	public static NonBlankValidator GET () {
		return VALIDATOR;
	}
	
	private NonBlankValidator () {}
	
	@Override
	public boolean validate (Object string) {
		return (string == null)? true : !((String) string).isBlank();
	}

}
