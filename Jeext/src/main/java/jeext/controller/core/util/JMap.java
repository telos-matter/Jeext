package jeext.controller.core.util;

import java.util.HashMap;
import java.util.Map;

import jeext.controller.Controller;

/**
 * <p>A {@link Class} that extends {@link HashMap}
 * to defines two additional search
 * methods, these methods have the particularity
 * that they search for the keys
 * with the {@link Object#equals(Object)} method
 * called from the keys of the {@link Map} and not
 * from the passed key or by hashing it
 * <p>Primarily used only in the {@link Controller}
 * 
 * @see #getEquals(Object)
 * @see #containsKeyEquals(Object)
 */
public class JMap <K, V> extends HashMap <K, V> {
	private static final long serialVersionUID = 1L;

	/**
	 * Same principle as {@link Map#containsKey(Object)}
	 * but uses {@link #getEquals(Object)}. Do see how
	 * {@link #getEquals(Object)} work
	 * 
	 * @return	<code>true</code> if the passed key
	 * already exists in the {@link Map}, <code>false</code> if not
	 * 
	 * @see #getEquals(Object)
	 */
	public boolean containsKeyEquals (Object key) {
		return getEquals(key) != null;
	}
	
	/**
	 * <p>Same principal as {@link Map#get(Object)}
	 * but the key is looked for by calling
	 * the {@link Object#equals(Object)} method
	 * from the keys of the {@link Map}
	 * <p>Note that it skips the null key
	 * if it contains one
	 * 
	 * @return	the value that is mapped, not to the passed key,
	 * but rather to the key
	 * in the {@link Map} that would return <code>true</code> for
	 * the {@link Object#equals(Object)} method with the passed key
	 * as a parameter. Or
	 * <code>null</code> if not found
	 */
	public V getEquals (Object key) {
		for (K mapKey : this.keySet()) {
			if (mapKey != null && mapKey.equals(key)) {
				return this.get(mapKey);
			}
		}
		
		return null;
	}
	
}
