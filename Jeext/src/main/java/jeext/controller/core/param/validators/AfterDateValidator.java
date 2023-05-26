package jeext.controller.core.param.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jeext.controller.core.param.validators.annotations.After;

/**
 * The implementation of the {@link After} {@link Validator}
 * for {@link LocalDate}
 */
public class AfterDateValidator implements Validator {

	private static final Map <LocalDate, AfterDateValidator> SET = new HashMap <> ();
	
	public static AfterDateValidator GET (LocalDate value) {
		AfterDateValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new AfterDateValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDate value;
	
	private AfterDateValidator (LocalDate value) {
		this.value = value;
	}

	@Override
	public boolean validate (Object locaDate) {
		return (locaDate == null)? true : ((LocalDate) locaDate).isAfter(value);
	}
	
}
