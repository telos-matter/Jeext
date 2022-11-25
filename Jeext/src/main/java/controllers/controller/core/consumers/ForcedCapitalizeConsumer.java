package controllers.controller.core.consumers;

import util.Strings;

public class ForcedCapitalizeConsumer implements Consumer {

	private static final ForcedCapitalizeConsumer CONSUMER = new ForcedCapitalizeConsumer();
	
	public static ForcedCapitalizeConsumer GET () {
		return CONSUMER;
	}
	
	private ForcedCapitalizeConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return Strings.forceCapitalize((String) object);
	}

}
