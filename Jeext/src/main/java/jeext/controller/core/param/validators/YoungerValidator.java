package jeext.controller.core.param.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jeext.controller.core.param.validators.annotations.Younger;
import jeext.util.Dates;
import jeext.util.Dates.PeriodHolder;

/**
 * The implementation of the {@link Younger} {@link Validator}
 */
public class YoungerValidator implements Validator {

	private static final Map <PeriodHolder, YoungerValidator> SET = new HashMap <> ();
	
	public static YoungerValidator GET (PeriodHolder value) {
		YoungerValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new YoungerValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private PeriodHolder value;
	
	private YoungerValidator (PeriodHolder value) {
		this.value = value;
	}

	@Override
	public boolean validate (Object localDate) {
		return (localDate == null)? true : !Dates.hasElapsed((LocalDate) localDate, value);
	}
	
}
