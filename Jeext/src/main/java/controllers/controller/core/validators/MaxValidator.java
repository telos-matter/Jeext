package controllers.controller.core.validators;

import java.util.HashMap;
import java.util.Map;

public class MaxValidator implements Validator {

	private static final Map <Double, MaxValidator> SET = new HashMap <> ();
	
	public static MaxValidator GET (double value) {
		MaxValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MaxValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private double value;
	
	private MaxValidator (double value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return ((double) object) <= value;
	}
	
}
