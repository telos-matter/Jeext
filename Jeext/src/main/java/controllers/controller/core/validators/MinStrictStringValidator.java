package controllers.controller.core.validators;

import java.util.HashMap;
import java.util.Map;

public class MinStrictStringValidator implements Validator {

	private static final Map <Integer, MinStrictStringValidator> SET = new HashMap <> ();
	
	public static MinStrictStringValidator GET (int value) {
		MinStrictStringValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MinStrictStringValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private int value;
	
	private MinStrictStringValidator (int value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((String) object).length() > value;
	}
	
}
