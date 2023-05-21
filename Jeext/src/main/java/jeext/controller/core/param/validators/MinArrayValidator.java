package jeext.controller.core.param.validators;

import java.util.Arrays;

public class MinArrayValidator implements Validator {

	public static MinArrayValidator GET (Class <?> type, int value, boolean strict) {
		return new MinArrayValidator (type, value, strict);
	}
	
	private Class <?> type;
	private int value;
	private boolean strict;
	
	private MinArrayValidator (Class <?> type, int value, boolean strict) {
		this.type = type;
		this.value = value;
		this.strict = strict;
	}

	@Override
	public boolean validate (Object array) {
		return (array == null)? true : check(array, type, value, strict);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> boolean check (Object array, Class <T> type, int value, boolean strict) {
		T [] _array = (T []) array;
		
		int count = (int) Arrays.stream(_array).filter(e -> e != null).count();
		
		return (strict)? count > value : count >= value;
	}
	
}
