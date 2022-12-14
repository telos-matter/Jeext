package controllers.controller.core.consumers;

import util.Strings;

public class CapitalizeConsumer implements Consumer {

	private static final CapitalizeConsumer CONSUMER = new CapitalizeConsumer();
	
	public static CapitalizeConsumer GET () {
		return CONSUMER;
	}
	
	private CapitalizeConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return Strings.capitalize((String) object);
	}

}
