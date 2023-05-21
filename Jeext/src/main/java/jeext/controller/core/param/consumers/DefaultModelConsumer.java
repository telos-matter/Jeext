package jeext.controller.core.param.consumers;

import jeext.dao.Manager;

public class DefaultModelConsumer implements Consumer {

	
	public static DefaultModelConsumer GET (Class <?> type, Object id) {
		return new DefaultModelConsumer (type, id);
	}
	
	private Class <?> type;
	private Object id;
	
	private DefaultModelConsumer (Class <?> type, Object id) {
		this.type = type;
		this.id = id;
	}

	@Override
	public Object consume (Object object) {
		return (object == null)? Manager.find(type, id) : object;
	}
	
}
