package controllers.controller.core.validators;

public class RequiredValidator implements Validator {

	private static final RequiredValidator REQUIRED = new RequiredValidator();
	
	public static RequiredValidator GET () {
		return REQUIRED;
	}
	
	private RequiredValidator () {}
	
	@Override
	public boolean validate(Object object) {
		return object != null;
	}

}
