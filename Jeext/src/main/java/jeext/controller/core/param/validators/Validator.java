package jeext.controller.core.param.validators;

@FunctionalInterface
public interface Validator {
	
	public boolean validate (Object object);
	
	public default boolean notValidate (Object object) {
		return ! validate(object);
	}
	
}
