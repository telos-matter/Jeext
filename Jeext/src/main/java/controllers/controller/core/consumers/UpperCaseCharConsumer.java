package controllers.controller.core.consumers;

import util.Strings;

public class UpperCaseCharConsumer implements Consumer {

	private static final UpperCaseCharConsumer CONSUMER = new UpperCaseCharConsumer();
	
	public static UpperCaseCharConsumer GET () {
		return CONSUMER;
	}
	
	private UpperCaseCharConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return Strings.toUpperCase((Character) object);
	}

}
