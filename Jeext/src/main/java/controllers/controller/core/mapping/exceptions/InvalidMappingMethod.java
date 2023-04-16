package controllers.controller.core.mapping.exceptions;

import java.lang.reflect.Method;
import java.util.Map;

import controllers.controller.Controller;
import controllers.controller.core.mapping.Mapping;
import util.exceptions.FailedRequirement;

/**
 * @see #InvalidMappingMethod(Class, Method, String)
 */
public class InvalidMappingMethod extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Thrown when a mapping method fails
	 * a requirement, i.e. it's invalid
	 * <p>Used mainly in {@link Controller#loadMappings(Class, Map)}
	 * and {@link Mapping#Mapping(Class, Method, controllers.controller.core.Access, models.core.Permission[], Boolean)}
	 */
	public InvalidMappingMethod (Class <?> controller, Method method, String reason) {
		super ("This mapping method '" +method +"' from this controller '" +controller+"' is invalid", reason);
	}
	
}