package jeext.controller.util.exceptions;

/**
 * @see #UnhandledException(Throwable)
 */
public class UnhandledException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Thrown when an {@link Exception} that should
	 * be handled, taken care of or is of the responsibility
	 * of the developer, isn't handled 
	 */
	public UnhandledException (Throwable type) {
		super(type);
		System.err.println("An unhandled exception was thrown:\n");
		type.printStackTrace();
	}

}
