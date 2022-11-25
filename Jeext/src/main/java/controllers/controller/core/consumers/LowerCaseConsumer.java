package controllers.controller.core.consumers;

import util.Strings;

public class LowerCaseConsumer implements Consumer {

	private static final LowerCaseConsumer CONSUMER = new LowerCaseConsumer();
	
	public static LowerCaseConsumer GET () {
		return CONSUMER;
	}
	
	private LowerCaseConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return Strings.toLowerCase((String) object);
	}

}
