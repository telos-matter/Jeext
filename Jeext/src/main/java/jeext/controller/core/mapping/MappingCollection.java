package jeext.controller.core.mapping;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import jeext.controller.core.HttpMethod;
import jeext.controller.core.Path;

// better named, MappingMap but that too much map
public class MappingCollection {

	private EnumMap <HttpMethod, Mapping> collection;
	
	public MappingCollection () {
		collection = new EnumMap <> (HttpMethod.class);
	}

	public boolean methodExists (HttpMethod method) {
		return collection.containsKey(method);
	}
	
	public Mapping getMapping (HttpMethod method) {
		return collection.get(method);
	}
	
	public void putMapping (HttpMethod method, Mapping mapping) {
		collection.put(method, mapping);
	}
	
	@Override
	public String toString() {
		return "" +collection;
	}
	
}
