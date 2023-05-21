package jeext.controller.core.param.validators;

public class MaxValidator implements Validator {

	public static MaxValidator GET (double value, boolean strict) {
		return new MaxValidator (value, strict);
	}
	
	private double value;
	private boolean strict;
	
	private MaxValidator (double value, boolean strict) {
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object number) {
		if (number == null) {
			return true;
		}
		
		double objectValue = ((Number) number).doubleValue();
		return (strict)? objectValue < value : objectValue <= value;
	}
	
}
