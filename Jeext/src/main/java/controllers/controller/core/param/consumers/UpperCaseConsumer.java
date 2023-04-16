package controllers.controller.core.param.consumers;

import util.Strings;

public class UpperCaseConsumer implements Consumer {

	private static final UpperCaseConsumer CONSUMER = new UpperCaseConsumer();
	
	public static UpperCaseConsumer GET () {
		return CONSUMER;
	}
	
	private UpperCaseConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return Strings.toUpperCase((String) object);
	}

}
