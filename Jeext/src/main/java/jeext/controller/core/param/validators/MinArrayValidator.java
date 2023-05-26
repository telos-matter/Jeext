package jeext.controller.core.param.validators;

import java.lang.reflect.Array;

import jeext.controller.core.param.validators.annotations.Min;

/**
 * The implementation of the {@link Min} {@link Validator}
 * for {@link Array}
 */
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
		
		int count = 0;
		for (int i = 0; i < _array.length; i++) {
			if (_array[i] != null) {
				count++;
			}
		}
		
		return (strict)? count > value : count >= value;
	}
	
}
