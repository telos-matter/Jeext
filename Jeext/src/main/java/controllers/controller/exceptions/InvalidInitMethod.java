package controllers.controller.exceptions;

import util.exceptions.FailedRequirement;

public class InvalidInitMethod extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	public InvalidInitMethod (Class <?> controller, String reason) {
		super ("The init method of this controller '" +controller.getName() +"' is invalid",reason);

	}
	
}