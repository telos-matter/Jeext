package jeext.controller.core.util;

import java.util.HashMap;
import java.util.Map;

public class JMap <K, V> extends HashMap <K, V> {
	private static final long serialVersionUID = 3557182132285188561L;

	public boolean containsKeyEquals (Object key) {
		return getEquals(key) != null;
	}
	
	/**
	 * note: skips over null keys
	 * @param key
	 * @return
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
