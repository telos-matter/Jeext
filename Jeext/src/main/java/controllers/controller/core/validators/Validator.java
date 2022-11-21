package controllers.controller.core.validators;

@FunctionalInterface
public interface Validator {
	
	// NOTICE they tolerate null

	public boolean validate (Object object);
	
	public default boolean notValidate (Object object) {
		return ! validate(object);
	}
	
}
