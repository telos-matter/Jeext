package controllers.controller.core.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import util.Dates;
import util.Dates.DateValuesHolder;

public class YoungerValidator implements Validator {

	private static final Map <DateValuesHolder, YoungerValidator> SET = new HashMap <> ();
	
	public static YoungerValidator GET (DateValuesHolder value) {
		YoungerValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new YoungerValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private DateValuesHolder value;
	
	private YoungerValidator (DateValuesHolder value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : !Dates.hasElapsed((LocalDate) object, value);
	}
	
}
