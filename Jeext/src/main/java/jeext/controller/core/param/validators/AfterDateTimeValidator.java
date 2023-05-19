package jeext.controller.core.param.validators;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AfterDateTimeValidator implements Validator {

	private static final Map <LocalDateTime, AfterDateTimeValidator> SET = new HashMap <> ();
	
	public static AfterDateTimeValidator GET (LocalDateTime value) {
		AfterDateTimeValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new AfterDateTimeValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDateTime value;
	
	private AfterDateTimeValidator (LocalDateTime value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((LocalDateTime) object).isAfter(value);
	}
	
}
