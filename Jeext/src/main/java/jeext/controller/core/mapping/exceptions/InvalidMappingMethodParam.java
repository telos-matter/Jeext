package jeext.controller.core.mapping.exceptions;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.param.Param;
import jeext.util.exceptions.FailedRequirement;

/**
 * @see #InvalidMappingMethodParam(Class, Method, Parameter, String)
 */
public class InvalidMappingMethodParam extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * Thrown when a {@link Parameter} in a {@link Mapping}
	 * (a.k.a. a {@link Param})
	 * is invalid or not used properly, this is of course
	 * different than {@link InvalidParameter}
	 */
	public InvalidMappingMethodParam (Class <?> webController, Method method, Parameter parameter, String reason) {
		super ("This parameter `" +parameter +"` from this mapping method `" +method +"` from this webController `" +webController +"` is invalid", reason);
	}
	
}