package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.LowerCase;
import jeext.util.Strings;

/**
 * <p>The implementation of the {@link LowerCase} {@link Consumer}
 * for {@link Character}
 */
public class LowerCaseCharConsumer implements Consumer {

	private static final LowerCaseCharConsumer CONSUMER = new LowerCaseCharConsumer();
	
	public static LowerCaseCharConsumer GET () {
		return CONSUMER;
	}
	
	private LowerCaseCharConsumer () {}
	
	@Override
	public Object consume (Object character) {
		return Strings.toLowerCase((Character) character);
	}

}
