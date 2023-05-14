package jeext.controller.core.param.validators;

import java.util.HashMap;
import java.util.Map;

public class MaxStrictStringValidator implements Validator {

	private static final Map <Integer, MaxStrictStringValidator> SET = new HashMap <> ();
	
	public static MaxStrictStringValidator GET (int value) {
		MaxStrictStringValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MaxStrictStringValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private int value;
	
	private MaxStrictStringValidator (int value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((String) object).length() < value;
	}
	
}
