package jeext.controller.core.param.consumers;

import java.util.HashMap;
import java.util.Map;

public class DefaultConsumer implements Consumer {

	private static final Map <Object, DefaultConsumer> SET = new HashMap <> ();
	
	public static DefaultConsumer GET (Object value) {
		DefaultConsumer consumer = SET.get(value);
		
		if (consumer == null) {
			consumer = new DefaultConsumer (value);
			SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private Object value;
	
	private DefaultConsumer (Object value) {
		this.value = value;
	}
	
	@Override
	public Object consume (Object object) {
		if (object == null || (object instanceof String stringObject && stringObject.isBlank())) {
			return value;
		}
		
		return object;
	}
	
}
