package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.LowerCase;
import jeext.util.Strings;

/**
 * <p>The implementation of the {@link LowerCase} {@link Consumer}
 */
public class LowerCaseConsumer implements Consumer {

	private static final LowerCaseConsumer CONSUMER = new LowerCaseConsumer();
	
	public static LowerCaseConsumer GET () {
		return CONSUMER;
	}
	
	private LowerCaseConsumer () {}
	
	@Override
	public Object consume (Object string) {
		return Strings.toLowerCase((String) string);
	}

}
