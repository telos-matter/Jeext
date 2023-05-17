package jeext.controller.core.param.consumers;

import java.util.HashMap;
import java.util.Map;

import jeext.dao.Manager;
import jeext.models_core.Model;

public class DefaultModelConsumer implements Consumer {

	private static final Map <Class <? extends Model <?>>, Map <Object, DefaultModelConsumer>> SET = new HashMap <> ();
	
	public static DefaultModelConsumer GET (Class <? extends Model <?>> type, Object id) {
		Map <Object, DefaultModelConsumer> SUB_SET = SET.get(type);
		
		if (SUB_SET == null) {
			SUB_SET = new HashMap <> ();
			SET.put(type, SUB_SET);
		}
		
		DefaultModelConsumer consumer = SUB_SET.get(id);
		
		if (consumer == null) {
			consumer = new DefaultModelConsumer (type, id);
			SUB_SET.put(id, consumer);
		}
		
		return consumer;
	}
	
	private Class <? extends Model <?>> type;
	private Object id;
	
	private DefaultModelConsumer (Class <? extends Model <?>> type, Object id) {
		this.type = type;
		this.id = id;
	}

	@Override
	public Object consume (Object object) {
		return (object == null)? Manager.find(type, id) : object;
	}
	
}
