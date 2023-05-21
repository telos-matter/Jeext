package jeext.controller.core.param.consumers;

import jeext.util.Strings;

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
