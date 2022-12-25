package controllers.controller.core.consumers;

import java.util.HashMap;
import java.util.Map;

import util.exceptions.UnsupportedType;

public class DefaultNumberConsumer implements Consumer {

	private static final Map <Class <?>, Map <Double, DefaultNumberConsumer>> SET = new HashMap <> ();
	
	public static DefaultNumberConsumer GET (Class <?> type, Double value) {
		Map <Double, DefaultNumberConsumer> SUB_SET = SET.get(type);
		
		if (SUB_SET == null) {
			SUB_SET = new HashMap <> ();
			SET.put(type, SUB_SET);
		}
		
		DefaultNumberConsumer consumer = SUB_SET.get(value);
		
		if (consumer == null) {
			consumer = new DefaultNumberConsumer (type, value);
			SUB_SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private Class <?> type;
	private double value;
	
	private DefaultNumberConsumer (Class <?> type, double value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public Object consume(Object object) {
		if (object != null) {
			return object;
			
		} else if (Integer.class.equals(type)) {
			return (Integer)(int) value;
			
		} else if (Float.class.equals(type)) {
			return (Float)(float) value;
			
		} else if (Double.class.equals(type)) {
			return (Double) value;
			
		} else if (Long.class.equals(type)) {
			return (Long)(long) value;
			
		} else if (Short.class.equals(type)) {
			return (Short)(short) value;
			
		} else if (Byte.class.equals(type)) {
			return (Byte)(byte) value;
			
		} else {
			throw new UnsupportedType(type);
		}
	}
	
}
