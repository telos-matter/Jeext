package controllers.controller.core.validators;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexValidator implements Validator {

	private static final Map <String, RegexValidator> SET = new HashMap <> ();
	
	public static RegexValidator GET (String value) {
		RegexValidator validator = SET.get(value);
		
		if (validator == null) {
			validator = new RegexValidator (value);
			SET.put(value, validator);
		}
		
		return validator;
	}
	
	private Pattern value;
	
	private RegexValidator (String value) {
		this.value = Pattern.compile(value);
	}

	@Override
	public boolean validate(Object object) {
		return (object == null)? true : value.matcher((String) object).find();
	}
	
}
