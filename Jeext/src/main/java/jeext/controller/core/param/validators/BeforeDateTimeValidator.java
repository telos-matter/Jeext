package jeext.controller.core.param.validators;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class BeforeDateTimeValidator implements Validator {

	private static final Map <LocalDateTime, BeforeDateTimeValidator> SET = new HashMap <> ();
	
	public static BeforeDateTimeValidator GET (LocalDateTime value) {
		BeforeDateTimeValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new BeforeDateTimeValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDateTime value;
	
	private BeforeDateTimeValidator (LocalDateTime value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((LocalDateTime) object).isBefore(value);
	}
	
}
