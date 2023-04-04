package util.exceptions;

import controllers.controller.exceptions.UnhandledUserException;

/**
* Different than {@link UnhandledUserException}
* 
* @see FailedAssertion
*/
public class UnhandledException extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	public UnhandledException (Exception e) {
		super ("Unhandled exception: " +e);
	}

}
