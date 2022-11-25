package controllers.controller.core.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class YoungerValidator implements Validator {

	private static final Map <LocalDate, YoungerValidator> SET = new HashMap <> ();
	
	public static YoungerValidator GET (LocalDate value) {
		YoungerValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new YoungerValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDate value;
	
	private YoungerValidator (LocalDate value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : Dates.has;
	}
	
}
