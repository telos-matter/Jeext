package jeext.controller.core.param.validators;

import java.util.List;

/**
 * The implementation of the {@link Min} {@link Validator}
 * for {@link List}
 */
public class MinListValidator implements Validator {

	public static MinListValidator GET (Class <?> type, int value, boolean strict) {
		return new MinListValidator (type, value, strict);
	}
	
	private Class <?> type;
	private int value;
	private boolean strict;
	
	private MinListValidator (Class <?> type, int value, boolean strict) {
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
		
		int count = 0;
		for (int i = 0; i < _list.size(); i++) {
			if (_list.get(i) != null) {
				count++;
			}
		}
		
		return (strict)? count > value : count >= value;
	}
	
}
