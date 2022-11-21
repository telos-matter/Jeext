package controllers.controller.core.validators;

public class NonBlankValidator implements Validator {

	private static final NonBlankValidator VALIDATOR = new NonBlankValidator();
	
	public static NonBlankValidator GET () {
		return VALIDATOR;
	}
	
	private NonBlankValidator () {}
	
	@Override
	public boolean validate(Object object) {
		return (object == null)? true : !((String) object).isBlank();
	}

}
