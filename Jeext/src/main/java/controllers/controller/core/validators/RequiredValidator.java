package controllers.controller.core.validators;

public class RequiredValidator implements Validator {

	private static final RequiredValidator VALIDATOR = new RequiredValidator();
	
	public static RequiredValidator GET () {
		return VALIDATOR;
	}
	
	private RequiredValidator () {}
	
	@Override
	public boolean validate(Object object) {
		return object != null;
	}

}
