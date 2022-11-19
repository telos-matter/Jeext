package controllers.controller.path;

public class Path {

	private String path;

	public Path(String path) {
		this.path = path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public boolean equals(Object other) {
	    if (this == other) {
	        return true;
	    }

	    return (other instanceof Path _other) && path.equals(_other.path);
	}
}
