package jeext.controller.core.param.consumers.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import jeext.controller.core.param.Param;
import jeext.controller.core.param.composer.annotations.ModelId;
import jeext.controller.core.param.consumers.Consumer;
import jeext.controller.core.param.consumers.DefaultConsumer;
import jeext.controller.core.param.consumers.DefaultModelConsumer;
import jeext.controller.core.param.consumers.DefaultNowDateConsumer;
import jeext.controller.core.param.consumers.DefaultNowDateTimeConsumer;
import jeext.controller.core.param.consumers.DefaultNowTimeConsumer;
import jeext.dao.Manager;
import jeext.model.Model;
import jeext.util.Parser;

/**
 * <p>This {@link Consumer} type of {@link Annotation} gives {@link Param}s
 * the value defined in {@link #value()} in case the incoming request does not have
 * a value for the {@link Param} annotated with this {@link Annotation},
 * or does but the value received cannot be casted to its type
 * <p>It goes on a number of types of {@link Param}s and the way
 * to use it depends on the type, and they are as follows:
 * <ul>
 * <li>{@link Model} - Write the ID by which to retrieve this {@link Model} from the DB
 * using the {@link Manager}. In order for it to work it is required
 * to identify which {@link Field}
 * in the {@link Model}s' {@link Class} is the ID with the {@link ModelId} {@link Annotation},
 * in addition that {@link Field}s' type should be one of these types:
 * {@link Number} (of course one of the subclasses
 * , such as {@link Integer}, {@link Float}.. and not the {@link Number}
 * {@link Class} it self), {@link String}, {@link Enum}, {@link LocalDate},
 * {@link LocalTime},
 * {@link LocalDateTime}, {@link Boolean} and {@link Character}.
 * {@link UUID} is not supported.
 * 
 * <li>{@link Number}
 * ({@link Integer}, {@link Float}, {@link Double},
 * {@link Long}, {@link Short}, {@link Byte}) - Simply write the value. Note that the
 * value should be compatible with the type, for example you can't use 3.14 with
 * an {@link Integer} or 399 with a {@link Byte} (the latter overflows
 * {@link Byte#MAX_VALUE})
 * <li>{@link String} - Write any string literal that you want
 * <li>{@link Enum} - Write the enum constant you want (as it
 * is written)
 * <li>{@link LocalDate} - Write a Date in the format of
 * {@link DateTimeFormatter#ISO_LOCAL_DATE}, for example 25-05-2023. Or `now`
 * to have the it default to today ({@link LocalDate#now()})
 * <li>{@link LocalTime} - Write a Time in the format of
 * {@link DateTimeFormatter#ISO_LOCAL_TIME}, for example 13:58:60.911, or 12:12.. .
 * Or `now` to have it default to now ({@link LocalTime#now()})
 * <li>{@link LocalDateTime} - Write a DateTime in the format
 * of {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}, for example 25-05-2023T13:58.. .
 * Or `now` to have it default to now ({@link LocalDateTime#now()})
 * <li>{@link Boolean} - Write a boolean value that can be parsed
 * with {@link Parser#parseBool(String)}
 * <li>{@link Character} - Write any single character string literal
 * </ul>
 * <p><b>Know that</b> in the case of a {@link String} type {@link Param},
 * this {@link Consumer} would give the default value defined to
 * the {@link String} {@link Param}
 * if it's blank (blank as defined in {@link String#isBlank()})
 * <p>The {@link Class}es that actual implement this {@link Consumer} are:
 * <ul>
 * <li>{@link DefaultConsumer}
 * <li>{@link DefaultModelConsumer}
 * <li>{@link DefaultNowDateConsumer}
 * <li>{@link DefaultNowTimeConsumer}
 * <li>{@link DefaultNowDateTimeConsumer}
 * </ul>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Default {
	
	/**
	 * @see Default
	 */
	String value();

}
