package jeext.controller.core.param.validators;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import jeext.controller.core.param.validators.annotations.After;

/**
 * The implementation of the {@link After} {@link Validator}
 * for {@link LocalTime}
 */
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
	public boolean validate (Object localTime) {
		return (localTime == null)? true : ((LocalTime) localTime).isAfter(value);
	}
	
}
