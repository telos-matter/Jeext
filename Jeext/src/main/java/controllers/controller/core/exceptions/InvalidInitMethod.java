package controllers.controller.core.exceptions;

import controllers.controller.Controller;
import controllers.controller.core.annotations.WebController;
import util.exceptions.FailedRequirement;

public class InvalidInitMethod extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Thrown when the init method of a {@link WebController}
	 * doesn't validate the specified requirements
	 * <p>Used mainly in {@link Controller#initControllers()}
	 */
	public InvalidInitMethod (Class <?> controller, String reason) {
		super ("The init method of this controller `" +controller +"` is invalid", reason);

	}
	
}