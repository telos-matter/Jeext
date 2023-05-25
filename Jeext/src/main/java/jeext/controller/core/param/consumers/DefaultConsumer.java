package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.Default;

/**
 * <p>The implementation of the {@link Default} {@link Consumer}
 * <p><b>Know that</b> in the case of {@link String}s, a blank
 * {@link String} (as indicated in {@link String#isBlank()})
 *  is also replaced by the default value
 */
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
