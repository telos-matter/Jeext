package jeext.controller.core.param.validators;

public class MinValidator implements Validator {

	public static MinValidator GET (double value, boolean strict) {
		return new MinValidator (value, strict);
	}
	
	private double value;
	private boolean strict;
	
	private MinValidator (double value, boolean strict) {
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object number) {
		if (number == null) {
			return true;
		}
		
		double objectValue = ((Number) number).doubleValue();
		return (strict)? objectValue > value : objectValue >= value;
	}
	
}
