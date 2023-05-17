package jeext.controller.core.mapping.exceptions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import jeext.controller.core.param.consumers.Consumer;
import jeext.util.exceptions.FailedRequirement;

/**
 * @see #InvalidMappingMethodParamConsumer(Class, Method, Parameter, Annotation, String)
 */
public class InvalidMappingMethodParamConsumer extends FailedRequirement {
	private static final long serialVersionUID = 1L;

	/**
	 * Thrown when a {@link Consumer}'s {@link Annotation}
	 * is invalid or not properly used
	 */
	public InvalidMappingMethodParamConsumer (Class <?> webController, Method method, Parameter parameter, Annotation consumer, String reason) {
		super ("This consumer `" +consumer +"` of this parameter `" +parameter +"` from this mapping method `" +method +"` from this webController `" +webController +"` is invalid", reason);
	}
	
}