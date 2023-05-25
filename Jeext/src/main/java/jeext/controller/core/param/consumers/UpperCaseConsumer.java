package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.UpperCase;
import jeext.util.Strings;

/**
 * <p>The implementation of the {@link UpperCase} {@link Consumer}
 */
public class UpperCaseConsumer implements Consumer {

	private static final UpperCaseConsumer CONSUMER = new UpperCaseConsumer();
	
	public static UpperCaseConsumer GET () {
		return CONSUMER;
	}
	
	private UpperCaseConsumer () {}
	
	@Override
	public Object consume (Object string) {
		return Strings.toUpperCase((String) string);
	}

}
