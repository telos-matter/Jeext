package jeext.controller.core.param.consumers;

public class FlagConsumer implements Consumer {

	private static final FlagConsumer CONSUMER = new FlagConsumer();
	
	public static FlagConsumer GET () {
		return CONSUMER;
	}
	
	private FlagConsumer () {}
	
	@Override
	public Object consume (Object object) {
		return (object == null)? Boolean.FALSE : (Boolean) object;
	}

}
