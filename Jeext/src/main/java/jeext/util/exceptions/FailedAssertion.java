package jeext.util.exceptions;

/**
 * <p>An exception base class meant for assertion
 * <p>As an end-user of the Jeext framework,
 * you should not encounter any exceptions
 * from this class or its subclasses
 * 
 * @author <a href="https://github.com/telos-matter">telos_matter</a>  
 */
public class FailedAssertion extends AssertionError {
	private static final long serialVersionUID = 1L;

	public FailedAssertion (String assertion) {
		super (assertion +"\nFOR ASSERTION. IF YOU SEE THIS EXCEPTION PLEASE CONTACT THE JEEXT FRAMEWORK DEV");
	}
	
}

