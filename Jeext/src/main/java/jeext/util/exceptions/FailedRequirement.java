package jeext.util.exceptions;

/**
 * <p>Base class for all failed requirements exceptions
 * <p>Thrown when the requirement(s) of a
 * process (usually methods) is (are) not met
 */
public class FailedRequirement extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public FailedRequirement (String requirement) {
		super ("Failed requirement:\n\t" +requirement);
	}
	
	public FailedRequirement (String statment, String reason) {
		this(statment +"\n\t\t-> " +reason);
	}
	
}
