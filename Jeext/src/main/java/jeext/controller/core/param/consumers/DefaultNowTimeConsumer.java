package jeext.controller.core.param.consumers;

import java.time.LocalTime;

import jeext.controller.core.param.consumers.annotations.Default;

/**
 * <p>The implementation of the {@link Default} {@link Consumer}
 * for {@link LocalTime} for the `now` case
 */
public class DefaultNowTimeConsumer implements Consumer {

	private static final DefaultNowTimeConsumer CONSUMER = new DefaultNowTimeConsumer();
	
	public static DefaultNowTimeConsumer GET () {
		return CONSUMER;
	}
	
	private DefaultNowTimeConsumer () {}
	
	@Override
	public Object consume (Object object) {
		return (object == null)? LocalTime.now() : object;
	}

}
