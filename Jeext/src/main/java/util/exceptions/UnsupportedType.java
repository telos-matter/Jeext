package util.exceptions;

public class UnsupportedType extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	public UnsupportedType (Object type) {
		super ("Unsupported type passed: " +type);
	}
	
}
