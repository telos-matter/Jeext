package controllers.controller.core.validators;

import java.util.HashMap;
import java.util.Map;

public class MinValidator implements Validator {

	private static final Map <Double, MinValidator> SET = new HashMap <> ();
	
	public static MinValidator GET (double value) {
		MinValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MinValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private double value;
	
	private MinValidator (double value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((Number) object).doubleValue() >= value;
	}
	
}
