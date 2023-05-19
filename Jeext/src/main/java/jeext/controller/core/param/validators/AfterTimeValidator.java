package jeext.controller.core.param.validators;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class AfterTimeValidator implements Validator {

	private static final Map <LocalTime, AfterTimeValidator> SET = new HashMap <> ();
	
	public static AfterTimeValidator GET (LocalTime value) {
		AfterTimeValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new AfterTimeValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalTime value;
	
	private AfterTimeValidator (LocalTime value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((LocalTime) object).isAfter(value);
	}
	
}
