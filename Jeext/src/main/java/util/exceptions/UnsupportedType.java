package util.exceptions;

/**
 *
 * @see FailedAssertion
 *
 */
public class UnsupportedType extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	public UnsupportedType (Object type) {
		super ("Unsupported type passed: " +type);
	}
	
}
