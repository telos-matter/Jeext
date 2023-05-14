package jeext.controller.core.param.validators;

import java.util.HashMap;
import java.util.Map;

public class MaxStrictValidator implements Validator {

	private static final Map <Double, MaxStrictValidator> SET = new HashMap <> ();
	
	public static MaxStrictValidator GET (double value) {
		MaxStrictValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MaxStrictValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private double value;
	
	private MaxStrictValidator (double value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((Number) object).doubleValue() < value;
	}
	
}
