package jeext.controller.core.param.consumers.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.consumers.Consumer;
import jeext.controller.core.param.consumers.UpperCaseCharConsumer;
import jeext.controller.core.param.consumers.UpperCaseConsumer;

/**
 * <p>This {@link Consumer} type of {@link Annotation} that goes on {@link Param}s
 * of type {@link String} or {@link Character}
 * turns what ever value received in the request to its upperCase version
 * <p>The {@link Class}es that actual implement this {@link Consumer} are:
 * <ul>
 * <li>{@link UpperCaseConsumer}
 * <li>{@link UpperCaseCharConsumer}
 * </ul>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface UpperCase {

}
