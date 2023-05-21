package jeext.controller.core.param.validators;

import java.util.List;

public class MaxListValidator implements Validator {

	public static MaxListValidator GET (Class <?> type, int value, boolean strict) {
		return new MaxListValidator (type, value, strict);
	}
	
	private Class <?> type;
	private int value;
	private boolean strict;
	
	private MaxListValidator (Class <?> type, int value, boolean strict) {
		this.type = type;
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object list) {
		return (list == null)? true : check(list, type, value, strict);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> boolean check (Object list, Class <T> type, int value, boolean strict) {
		List <T> _list = (List <T>) list;
		
		int count = (int) _list.stream().filter(e -> e != null).count();
		
		return (strict)? count < value : count <= value;
	}
	
}
