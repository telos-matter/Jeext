package jeext.controller.core.param.consumers;

import jeext.controller.core.param.consumers.annotations.Default;
import jeext.dao.Manager;
import jeext.model.Model;

/**
 * <p>The implementation of the {@link Default} {@link Consumer}
 * for {@link Model}
 */
public class DefaultModelConsumer implements Consumer {

	
	public static DefaultModelConsumer GET (Class <?> type, Object id) {
		return new DefaultModelConsumer (type, id);
	}
	
	private Class <?> type;
	private Object id;
	
	private DefaultModelConsumer (Class <?> type, Object id) {
		// FIXME use model.clazz.find instead, that way it doesn't depend on the manager and the user is free to change the manager
		this.type = type;
		this.id = id;
	}

	@Override
	public Object consume (Object object) {
		return (object == null)? Manager.find(type, id) : object;
	}
	
}
