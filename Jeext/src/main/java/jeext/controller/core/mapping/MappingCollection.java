package jeext.controller.core.mapping;

import java.util.EnumMap;
import java.util.Map;

import jeext.controller.Controller;
import jeext.controller.core.HttpMethod;
import jeext.controller.core.annotations.WebMapping;

/**
 * <p>A better name for this class would've been MappingMap
 * but that is too much map
 * <p>It is a {@link Map} (more specifically an {@link EnumMap})
 * of the different {@link HttpMethod}s and their corresponding
 * {@link Mapping}s
 * 
 * @see Controller
 * @see Mapping
 * @see WebMapping
 */
public class MappingCollection {

	/**
	 * The {@link EnumMap} that maps each {@link HttpMethod}
	 * to its corresponding {@link Mapping}
	 */
	private EnumMap <HttpMethod, Mapping> collection;
	
	/**
	 * A constructor that instantiates the {@link EnumMap}
	 */
	public MappingCollection () {
		collection = new EnumMap <> (HttpMethod.class);
	}

	/**
	 * @return whether or not this collection
	 * already has that {@link HttpMethod}
	 */
	public boolean methodExists (HttpMethod method) {
		return collection.containsKey(method);
	}
	
	/**
	 * @return the {@link Mapping} that is
	 * mapped to that {@link HttpMethod}, or <code>null</code>
	 * if it does not exist
	 */
	public Mapping getMapping (HttpMethod method) {
		return collection.get(method);
	}
	
	/**
	 * Puts the passed {@link HttpMethod} with
	 * its corresponding {@link Mapping} in the
	 * {@link EnumMap}
	 */
	public void putMapping (HttpMethod method, Mapping mapping) {
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
