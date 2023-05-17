package jeext.controller.core.mapping.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jeext.controller.core.param.validators.Validator;
import jeext.util.exceptions.FailedRequirement;

/**
 * @see #InvalidMappingMethodParamValidator(Class, Method, Parameter, Annotation, String)
 */
public class InvalidMappingMethodParamValidator extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * Thrown when a {@link Validator}'s {@link Annotation}
	 * is invalid or not properly used
	 */
	public InvalidMappingMethodParamValidator (Class <?> webController, Method method, Parameter parameter, Annotation validator, String reason) {
		super ("This validator `" +validator +"` of this parameter `" +parameter +"' from this mapping method `" +method +"` from this webController `" +webController +"` is invalid", reason);
	}
	
}