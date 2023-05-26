package jeext.controller.core.param.validators;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jeext.controller.core.param.validators.annotations.Before;

/**
 * The implementation of the {@link Before} {@link Validator}
 * for {@link LocalDateTime}
 */
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
	public boolean validate (Object localDateTime) {
		return (localDateTime == null)? true : ((LocalDateTime) localDateTime).isBefore(value);
	}
	
}
