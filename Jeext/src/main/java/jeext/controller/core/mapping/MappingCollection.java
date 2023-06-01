package jeext.controller.core.mapping;

import java.util.EnumMap;
import java.util.Map;

import jeext.controller.Controller;
import jeext.controller.core.HTTPMethod;
import jeext.controller.core.annotations.WebMapping;

/**
 * <p>A better name for this class would've been MappingMap
 * but that is too much map
 * <p>It is a {@link Map} (more specifically an {@link EnumMap})
 * of the different {@link HTTPMethod}s and their corresponding
 * {@link Mapping}s
 * 
 * @see Controller
 * @see Mapping
 * @see WebMapping
 */
public class MappingCollection {

	/**
	 * The {@link EnumMap} that maps each {@link HTTPMethod}
	 * to its corresponding {@link Mapping}
	 */
	private EnumMap <HTTPMethod, Mapping> collection;
	
	/**
	 * A constructor that instantiates the {@link EnumMap}
	 */
	public MappingCollection () {
		collection = new EnumMap <> (HTTPMethod.class);
	}

	/**
	 * @return whether or not this collection
	 * already has that {@link HTTPMethod}
	 */
	public boolean methodExists (HTTPMethod method) {
		return collection.containsKey(method);
	}
	
	/**
	 * @return the {@link Mapping} that is
	 * mapped to that {@link HTTPMethod}, or <code>null</code>
	 * if it does not exist
	 */
	public Mapping getMapping (HTTPMethod method) {
		return collection.get(method);
	}
	
	/**
	 * Puts the passed {@link HTTPMethod} with
	 * its corresponding {@link Mapping} in the
	 * {@link EnumMap}
	 */
	public void putMapping (HTTPMethod method, Mapping mapping) {
		collection.put(method, mapping);
	}
	
	/**
	 * What do you think it does?
	 */
	@Override
	public String toString() {
		return "" +collection;
	}
	
}
