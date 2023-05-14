package jeext.controller.core.exceptions;

import jeext.controller.Controller;
import jeext.controller.core.annotations.WebController;
import jeext.util.exceptions.FailedRequirement;

/**
 *
 * @see #InvalidInitMethod(Class, String)
 *
 */
public class InvalidInitMethod extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Thrown when the init method of a {@link WebController}
	 * doesn't validate the specified requirements
	 * <p>Used mainly in {@link Controller#initControllers()}
	 */
	public InvalidInitMethod (Class <?> controller, String reason) {
		super ("The init method of this webController `" +controller +"` is invalid", reason);

	}
	
}