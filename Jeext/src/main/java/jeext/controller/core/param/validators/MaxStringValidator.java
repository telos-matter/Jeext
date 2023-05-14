package jeext.controller.core.param.validators;

import java.util.HashMap;
import java.util.Map;

public class MaxStringValidator implements Validator {

	private static final Map <Integer, MaxStringValidator> SET = new HashMap <> ();
	
	public static MaxStringValidator GET (int value) {
		MaxStringValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MaxStringValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private int value;
	
	private MaxStringValidator (int value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((String) object).length() <= value;
	}
	
}
