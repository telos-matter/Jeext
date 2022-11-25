package controllers.controller.core.consumers;

import java.time.LocalDate;

public class DefaultNowDateConsumer implements Consumer {

	private static final DefaultNowDateConsumer CONSUMER = new DefaultNowDateConsumer();
	
	public static DefaultNowDateConsumer GET () {
		return CONSUMER;
	}
	
	private DefaultNowDateConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return (object == null)? LocalDate.now() : object;
	}

}
