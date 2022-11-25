package controllers.controller.core.consumers;

import java.util.HashMap;
import java.util.Map;

public class DefaultConsumer implements Consumer {

	private static final Map <String, DefaultConsumer> SET = new HashMap <> ();
	
	public static DefaultConsumer GET (String value) {
		DefaultConsumer consumer = SET.get(value);
		
		if (consumer == null) {
			consumer = new DefaultConsumer (value);
			SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private String value;
	
	private DefaultConsumer (String value) {
		this.value = value;
	}

	@Override
	public Object consume(Object object) {
		return (object == null)? value : object;
	}
	
}
