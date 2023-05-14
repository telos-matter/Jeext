package jeext.controller.core.param.validators;

import java.util.HashMap;
import java.util.Map;

public class MinStringValidator implements Validator {

	private static final Map <Integer, MinStringValidator> SET = new HashMap <> ();
	
	public static MinStringValidator GET (int value) {
		MinStringValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new MinStringValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private int value;
	
	private MinStringValidator (int value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((String) object).length() >= value;
	}
	
}
