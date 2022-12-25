package controllers.controller.core;

import controllers.controller.core.annotations.WebController;

public enum Access {

	/**
	 * Should not be used with {@link WebController}s
	 */
	DEFAULT,
	
	NONE,
	ALL,
	IDENTIFIED,
	ANONYMOUS;
	
}
