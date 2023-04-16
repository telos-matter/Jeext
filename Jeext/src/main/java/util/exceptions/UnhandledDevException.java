package util.exceptions;

import controllers.controller.core.util.exceptions.UnhandledException;

/**
* Different than {@link UnhandledException}
* 
* @see FailedAssertion
*/
public class UnhandledDevException extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	public UnhandledDevException (Exception e) {
		super ("Unhandled exception: " +e);
	}

}
