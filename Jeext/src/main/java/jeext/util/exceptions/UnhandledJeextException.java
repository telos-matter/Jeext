package jeext.util.exceptions;

import jeext.controller.util.exceptions.UnhandledException;

/**
* Different than {@link UnhandledException}
* 
* @see FailedAssertion
*/
public class UnhandledJeextException extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	/**
	 * @see UnhandledJeextException
	 */
	public UnhandledJeextException (Throwable e) {
		super ("Unhandled Jeext exception: " +e);
		e.printStackTrace();
	}

}
