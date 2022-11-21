package controllers.controller.exceptions;

public class UnsupportedType extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnsupportedType (Class <?> type, boolean byUser) {
		super ("Unsupported type passed: " +type +"[From user: " +byUser +"]");
	}
	
}
