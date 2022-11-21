package controllers.controller.core.consumers;

public class FlagConsumer implements Consumer {

	private static final FlagConsumer FLAG = new FlagConsumer();
	
	public static FlagConsumer GET () {
		return FLAG;
	}
	
	private FlagConsumer () {}
	
	@Override
	public Object consume(Object object) {
		return (object == null)? Boolean.FALSE : (Boolean) object;
	}

}
