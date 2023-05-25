package jeext.controller.core.param.consumers;

import java.time.LocalDate;

import jeext.controller.core.param.consumers.annotations.Default;

/**
 * <p>The implementation of the {@link Default} {@link Consumer}
 * for {@link LocalDate} for the `now` case
 */
public class DefaultNowDateConsumer implements Consumer {

	private static final DefaultNowDateConsumer CONSUMER = new DefaultNowDateConsumer();
	
	public static DefaultNowDateConsumer GET () {
		return CONSUMER;
	}
	
	private DefaultNowDateConsumer () {}
	
	@Override
	public Object consume (Object object) {
		return (object == null)? LocalDate.now() : object;
	}

}
