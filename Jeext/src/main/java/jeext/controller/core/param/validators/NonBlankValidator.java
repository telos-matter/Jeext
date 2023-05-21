package jeext.controller.core.param.validators;

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
