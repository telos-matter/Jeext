package controllers.controller.exceptions;

public class UnhandledUserException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnhandledUserException (Throwable type) {
		super ("An unhandled exception was thrown:\n\t->" +type );
	}
	
}

