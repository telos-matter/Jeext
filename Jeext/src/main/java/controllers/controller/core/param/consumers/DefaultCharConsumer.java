package controllers.controller.core.param.consumers;

import java.util.HashMap;
import java.util.Map;

public class DefaultCharConsumer implements Consumer {

	private static final Map <Character, DefaultCharConsumer> SET = new HashMap <> ();
	
	public static DefaultCharConsumer GET (Character value) {
		DefaultCharConsumer consumer = SET.get(value);
		
		if (consumer == null) {
			consumer = new DefaultCharConsumer (value);
			SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private Character value;
	
	private DefaultCharConsumer (Character value) {
		this.value = value;
	}

	@Override
	public Object consume(Object object) {
		return (object == null)? value : object;
	}
	
}
