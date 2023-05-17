package jeext.controller.core.param.consumers;

import jeext.util.Strings;

public class LowerCaseCharConsumer implements Consumer {

	private static final LowerCaseCharConsumer CONSUMER = new LowerCaseCharConsumer();
	
	public static LowerCaseCharConsumer GET () {
		return CONSUMER;
	}
	
	private LowerCaseCharConsumer () {}
	
	@Override
	public Object consume (Object object) {
		return Strings.toLowerCase((Character) object);
	}

}
