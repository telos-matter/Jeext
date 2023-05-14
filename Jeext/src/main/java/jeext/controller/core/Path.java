package jeext.controller.core;

import jeext.controller.core.mapping.MappingCollection;

// TODO the * version should also take care of /controllers/* == /controllers, not sure
// TODO rn its just a simple string , but usefull in case later on i wanna make it a more complex url that allows * for example
public class Path {

	private String path;
	
	public Path (String path) {
		this.path = path;
	}
	
	@Override
	public boolean equals(Object obj) {
//		System.out.println("? " + this.path + " : " + obj);
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
