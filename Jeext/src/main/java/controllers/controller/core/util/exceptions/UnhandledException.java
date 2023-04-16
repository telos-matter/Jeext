package controllers.controller.core.util.exceptions;

/**
 * Thrown when an {@link Exception} that should
 * be handled, taken care of or is of the responsibility
 * of the developer, isn't handled 
 */
public class UnhandledException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnhandledException (Throwable type) {
		super ("An unhandled exception was thrown:\n");
		type.printStackTrace();
	}

}
