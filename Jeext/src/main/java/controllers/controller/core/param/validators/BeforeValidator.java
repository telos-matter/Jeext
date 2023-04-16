package controllers.controller.core.param.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BeforeValidator implements Validator {

	private static final Map <LocalDate, BeforeValidator> SET = new HashMap <> ();
	
	public static BeforeValidator GET (LocalDate value) {
		BeforeValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new BeforeValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDate value;
	
	private BeforeValidator (LocalDate value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((LocalDate) object).isBefore(value);
	}
	
}
