package util.exceptions;

public class PassedNull extends FailedAssertion {
	private static final long serialVersionUID = 1L;

	public static <T> void check (T object, Class <T> clazz) {
		if (object == null) {
			throw new PassedNull(clazz);
		}
	}
	
	public PassedNull (Class <?> clazz) {
		super ("Null instance of " +clazz +" passed");
	}

}
