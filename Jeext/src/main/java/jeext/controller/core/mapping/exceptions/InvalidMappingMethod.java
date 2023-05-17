package jeext.controller.core.mapping.exceptions;

import java.lang.reflect.Method;
import java.util.Map;

import jeext.controller.Controller;
import jeext.controller.core.mapping.Mapping;
import jeext.util.exceptions.FailedRequirement;

/**
 * @see #InvalidMappingMethod(Class, Method, String)
 */
public class InvalidMappingMethod extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Thrown when a mapping method fails
	 * a requirement, i.e. it's invalid
	 * <p>Used mainly in {@link Controller#loadMappings(Class, Map)}
	 * and {@link Mapping#Mapping(Class, Method, jeext.controller.core.Access, models.core.Permission[], Boolean)}
	 */
	public InvalidMappingMethod (Class <?> webController, Method method, String reason) {
		super ("This mapping method `" +method +"` from this webController `" +webController+"` is invalid", reason);
	}
	
}