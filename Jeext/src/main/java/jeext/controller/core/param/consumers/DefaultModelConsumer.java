package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.Default;
import jeext.model.Model;
import jeext.util.exceptions.PassedNull;

/**
 * <p>The implementation of the {@link Default} {@link Consumer}
 * for {@link Model}
 */
public class DefaultModelConsumer implements Consumer {

	
	public static DefaultModelConsumer GET (Model <?> instance, Object id) {
		PassedNull.check(instance, Model.class);
		return new DefaultModelConsumer (instance, id);
	}
	
	private Model <?> instance;
	private Object id;
	
	private DefaultModelConsumer (Model <?> instance, Object id) {
		this.instance = instance;
		this.id = id;
	}

	@Override
	public Object consume (Object object) {
		return (object == null)? instance.clazz.find(id) : object;
	}
	
}
