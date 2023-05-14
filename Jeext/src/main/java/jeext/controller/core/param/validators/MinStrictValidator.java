package jeext.controller.core.param.validators;

import java.util.HashMap;
import java.util.Map;

public class MinStrictValidator implements Validator {

	private static final Map <Double, MinStrictValidator> SET = new HashMap <> ();
	
	public static MinStrictValidator GET (double value) {
		MinStrictValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MinStrictValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private double value;
	
	private MinStrictValidator (double value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((Number) object).doubleValue() > value;
	}
	
}
