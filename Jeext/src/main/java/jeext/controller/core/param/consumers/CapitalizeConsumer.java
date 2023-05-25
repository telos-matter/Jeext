package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.Capitalize;
import jeext.util.Strings;

/**
 * The implementation of the {@link Capitalize} {@link Consumer}
 */
public class CapitalizeConsumer implements Consumer {

	private static final CapitalizeConsumer TRUE_CONSUMER = new CapitalizeConsumer(true);
	private static final CapitalizeConsumer FALSE_CONSUMER = new CapitalizeConsumer(false);
	
	public static CapitalizeConsumer GET (boolean forced) {
		if (forced) {
			return TRUE_CONSUMER;
		} else {
			return FALSE_CONSUMER;
		}
	}
	
	private boolean forced;
	
	private CapitalizeConsumer (boolean forced) {
		this.forced = forced;
	}
	
	@Override
	public Object consume(Object string) {
		if (forced) {
			return Strings.forceCapitalize((String) string);
		} else {
			return Strings.capitalize((String) string);
		}
	}

}
