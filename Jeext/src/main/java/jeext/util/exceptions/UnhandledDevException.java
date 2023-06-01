package jeext.util.exceptions;

import jeext.controller.util.exceptions.UnhandledException;

/**
* Different than {@link UnhandledException}
* 
* @see FailedAssertion
*/
public class UnhandledDevException extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	/**
	 * @see UnhandledDevException
	 */
	public UnhandledDevException (Exception e) {
		super ("Unhandled exception: " +e);
	}

}
