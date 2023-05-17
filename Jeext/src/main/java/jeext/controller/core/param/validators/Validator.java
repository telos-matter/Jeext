package jeext.controller.core.param.validators;

@FunctionalInterface
public interface Validator {
	
	public boolean validate (Object object);
	
}
