package jeext.controller.core.param.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import jeext.controller.core.param.Param;
import jeext.model.Model;

/**
 * <p>Points out the ID {@link Field}
 * of a {@link Model}
 * <p>Any {@link Model} that is going to be used
 * as a {@link Param} in any way should indicate
 * which field is the ID with this {@link Annotation}
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface MID {

}
