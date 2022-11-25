package controllers.controller.core.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import util.Dates;
import util.Dates.DateValuesHolder;

public class OlderValidator implements Validator {

	private static final Map <DateValuesHolder, OlderValidator> SET = new HashMap <> ();
	
	public static OlderValidator GET (DateValuesHolder value) {
		OlderValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new OlderValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private DateValuesHolder value;
	
	private OlderValidator (DateValuesHolder value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : Dates.hasElapsed((LocalDate) object, value);
	}
	
}
