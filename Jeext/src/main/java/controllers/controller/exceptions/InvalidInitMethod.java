package controllers.controller.exceptions;

public class InvalidInitMethod extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidInitMethod (Class <?> controller, String reason) {
		super ("The init method of this controller: '" +controller.getName() +"' is invalid\n(Reason: " +reason +")");

	}
	
}