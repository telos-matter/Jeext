package jeext.controller.core.param.validators;


/**
 * The implementation of the {@link Min} {@link Validator}
 * for {@link String}
 */
public class MinStringValidator implements Validator {
	
	public static MinStringValidator GET (int value, boolean strict) {
		return new MinStringValidator (value, strict);
	}
	
	private int value;
	private boolean strict;

	private MinStringValidator (int value, boolean strict) {
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate(Object string) {
		if (string == null) {
			return true;
		}
		
		int objectValue = ((String) string).length();
		return (strict)? objectValue > value : objectValue >= value;
	}
	
}
