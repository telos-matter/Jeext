package controllers.controller.core.validators;

public class Required implements Validator {

	public static final Required REQUIRED = new Required();
	
	private Required () {}
	
	@Override
	public boolean validate(Object object) {
		return object != null;
	}

}
