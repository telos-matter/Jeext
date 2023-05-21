package jeext.controller.core.param.validators;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BeforeDateValidator implements Validator {

	private static final Map <LocalDate, BeforeDateValidator> SET = new HashMap <> ();
	
	public static BeforeDateValidator GET (LocalDate value) {
		BeforeDateValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new BeforeDateValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private LocalDate value;
	
	private BeforeDateValidator (LocalDate value) {
		this.value = value;
	}

	@Override
	public boolean validate (Object localDate) {
		return (localDate == null)? true : ((LocalDate) localDate).isBefore(value);
	}
	
}
