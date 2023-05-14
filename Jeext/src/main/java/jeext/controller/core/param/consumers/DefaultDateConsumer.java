package jeext.controller.core.param.consumers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DefaultDateConsumer implements Consumer {

	private static final Map <LocalDate, DefaultDateConsumer> SET = new HashMap <> ();
	
	public static DefaultDateConsumer GET (LocalDate value) {
		DefaultDateConsumer consumer = SET.get(value);
		
		if (consumer == null) {
			consumer = new DefaultDateConsumer (value);
			SET.put(value, consumer);
		}
		
		return consumer;
	}
	
	private LocalDate value;
	
	private DefaultDateConsumer (LocalDate value) {
		this.value = value;
	}

	@Override
	public Object consume(Object object) {
		return (object == null)? value : object;
	}
	
}
