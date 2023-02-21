package util.exceptions;

public class UnhandledException extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	public UnhandledException (Exception e) {
		super ("Unhandled exception: " +e);
	}

}
