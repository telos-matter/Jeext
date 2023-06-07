package jeext.controller.core;

/**
 * Simple version rn 
 *  no URL-encoded parameters
 * TODO implement the * version, keep in mind /xxx/* == /xxx (Maybe not sure)
 */
public class Path {

	private String path;
	
	public Path (String path) {
		this.path = path;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String other) {
			return this.path.equals(other);
			
		} else if (obj instanceof Path other) {
			return this.path.equals(other.path);
			
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return "`" +path +"`";
	}
	
}
