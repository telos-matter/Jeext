package controllers.controller.core.param.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AfterValidator implements Validator {

	private static final Map <LocalDate, AfterValidator> SET = new HashMap <> ();
	
	public static AfterValidator GET (LocalDate value) {
		AfterValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new AfterValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDate value;
	
	private AfterValidator (LocalDate value) {
		this.value = value;
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : ((LocalDate) object).isAfter(value);
	}
	
}
