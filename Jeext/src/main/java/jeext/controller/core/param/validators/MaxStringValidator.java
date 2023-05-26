package jeext.controller.core.param.validators;


import jeext.controller.core.param.validators.annotations.Max;

/**
 * The implementation of the {@link Max} {@link Validator}
 * for {@link String}
 */
public class MaxStringValidator implements Validator {
	
	public static MaxStringValidator GET (int value, boolean strict) {
		return new MaxStringValidator (value, strict);
	}
	
	private int value;
	private boolean strict;

	private MaxStringValidator (int value, boolean strict) {
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object string) {
		if (string == null) {
			return true;
		}
		
		int objectValue = ((String) string).length();
		return (strict)? objectValue < value : objectValue <= value;
	}
	
}
