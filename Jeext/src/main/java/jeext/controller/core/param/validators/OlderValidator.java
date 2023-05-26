package jeext.controller.core.param.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jeext.controller.core.param.validators.annotations.Older;
import jeext.util.Dates;
import jeext.util.Dates.PeriodHolder;

/**
 * The implementation of the {@link Older} {@link Validator}
 */
public class OlderValidator implements Validator {

	private static final Map <PeriodHolder, OlderValidator> SET = new HashMap <> ();
	
	public static OlderValidator GET (PeriodHolder value) {
		OlderValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new OlderValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private PeriodHolder value;
	
	private OlderValidator (PeriodHolder value) {
		this.value = value;
	}

	@Override
	public boolean validate (Object localDate) {
		return (localDate == null)? true : Dates.hasElapsed((LocalDate) localDate, value);
	}
	
}
