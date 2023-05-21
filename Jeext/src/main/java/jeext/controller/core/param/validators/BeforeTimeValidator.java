package jeext.controller.core.param.validators;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class BeforeTimeValidator implements Validator {

	private static final Map <LocalTime, BeforeTimeValidator> SET = new HashMap <> ();
	
	public static BeforeTimeValidator GET (LocalTime value) {
		BeforeTimeValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new BeforeTimeValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalTime value;
	
	private BeforeTimeValidator (LocalTime value) {
		this.value = value;
	}

	@Override
	public boolean validate (Object localTime) {
		return (localTime == null)? true : ((LocalTime) localTime).isBefore(value);
	}
	
}
