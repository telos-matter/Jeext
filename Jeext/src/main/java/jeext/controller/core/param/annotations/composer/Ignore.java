package jeext.controller.core.param.annotations.composer;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import jeext.model.Model;

/**
 * <p>This {@link Annotation} indicates that a {@link Field}
 * in a {@link Model} should be ignored by {@link Composed}.
 * And thus the {@link Field} won't be retrieved or filled
 * <p>Obviously, it is mutually exclusive with the {@link ComposeWith}
 * {@link Annotation}
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Ignore {

}
