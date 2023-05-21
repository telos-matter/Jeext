package jeext.controller.core.param.consumers;

import java.time.LocalDateTime;

public class DefaultNowDateTimeConsumer implements Consumer {

	private static final DefaultNowDateTimeConsumer CONSUMER = new DefaultNowDateTimeConsumer();
	
	public static DefaultNowDateTimeConsumer GET () {
		return CONSUMER;
	}
	
	private DefaultNowDateTimeConsumer () {}
	
	@Override
	public Object consume (Object object) {
		return (object == null)? LocalDateTime.now() : object;
	}

}
