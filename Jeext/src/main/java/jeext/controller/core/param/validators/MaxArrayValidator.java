package jeext.controller.core.param.validators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaxArrayValidator implements Validator {

	private static final Map <Class <?>, Map <Double, MaxArrayValidator>> SET = new HashMap <> ();
	
	public static MaxArrayValidator GET (Class <?> type, double value) {
		Map <Double, MaxArrayValidator> SUB_SET = SET.get(type);
		
		if (SUB_SET == null) {
			SUB_SET = new HashMap <> ();
			SET.put(type, SUB_SET);
		}
		
		MaxArrayValidator consumer = SUB_SET.get(value);
		
		if (consumer == null) {
			consumer = new MaxArrayValidator (type, value);
			SUB_SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private Class <?> type;
	private double value;
	
	private MaxArrayValidator (Class <?> type, double value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public boolean validate (Object object) {
		return (object == null)? true : check(object, type, value);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> boolean check (Object array, Class <T> type, double value) {
		T [] _array = (T []) array;
		
		long count = Arrays.stream(_array).filter(e -> e != null).count();
		
		return count <= value;
	}
	
}
