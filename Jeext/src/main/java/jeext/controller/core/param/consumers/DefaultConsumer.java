package jeext.controller.core.param.consumers;

public class DefaultConsumer implements Consumer {

	public static DefaultConsumer GET (Object value) {
		return new DefaultConsumer (value);
	}
	
	private Object value;
	
	private DefaultConsumer (Object value) {
		this.value = value;
	}
	
	@Override
	public Object consume (Object object) {
		if (object == null || (object instanceof String stringObject && stringObject.isBlank())) {
			return value;
		}
		
		return object;
	}
	
}
