package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.UpperCase;
import jeext.util.Strings;

/**
 * <p>The implementation of the {@link UpperCase} {@link Consumer}
 * for {@link Character}
 */
public class UpperCaseCharConsumer implements Consumer {

	private static final UpperCaseCharConsumer CONSUMER = new UpperCaseCharConsumer();
	
	public static UpperCaseCharConsumer GET () {
		return CONSUMER;
	}
	
	private UpperCaseCharConsumer () {}
	
	@Override
	public Object consume (Object character) {
		return Strings.toUpperCase((Character) character);
	}

}
