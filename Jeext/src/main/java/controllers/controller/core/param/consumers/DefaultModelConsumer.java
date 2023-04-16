package controllers.controller.core.param.consumers;

import java.util.HashMap;
import java.util.Map;

import dao.Manager;

public class DefaultModelConsumer implements Consumer {

	private static final Map <Class <?>, Map <Long, DefaultModelConsumer>> SET = new HashMap <> ();
	
	public static DefaultModelConsumer GET (Class <?> type, Long value) {
		Map <Long, DefaultModelConsumer> SUB_SET = SET.get(type);
		
		if (SUB_SET == null) {
			SUB_SET = new HashMap <> ();
			SET.put(type, SUB_SET);
		}
		
		DefaultModelConsumer consumer = SUB_SET.get(value);
		
		if (consumer == null) {
			consumer = new DefaultModelConsumer (type, value);
			SUB_SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private Class <?> type;
	private long value;
	
	private DefaultModelConsumer (Class <?> type, long value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public Object consume(Object object) {
		return (object == null)? Manager.find(type, value) : object;
	}
	
}
