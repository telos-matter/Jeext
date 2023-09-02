package jeext.controller.core.param;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParam;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamConsumer;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamValidator;
import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.consumers.*;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.types.FileType;
import jeext.controller.core.param.validators.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.model.Model;
import jeext.util.Dates.PeriodHolder;
import jeext.util.exceptions.UnhandledJeextException;
import jeext.util.exceptions.UnsupportedType;
import jeext.util.Parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>A {@link Param} is <b>any</b> {@link Parameter} in a
 * {@link Mapping} {@link Method} (except of course
 * {@link HttpServletRequest} and {@link HttpServletResponse})
 * that is expected as a parameter in the incoming HTTP request
 * <p>It provides an easy way to retrieve, check and operate on
 * the parameters that a user sends
 * <p>The tedious procedure of calling
 * <code>request.getParameter(..)</code>, checking if its not
 * <code>null</code>, casting to
 * the appropriate type and then checking if the casting went well.
 * All of that gets automated! And a lot more.
 * <p>The allowed types of {@link Parameter}s are: 
 * <ul>
 * <li>{@link Model}
 * <li>{@link Number} ({@link Integer}, {@link Float}, {@link Double}, {@link Long}, {@link Short}, {@link Byte})
 * <li>{@link Enum}
 * <li>{@link FileType}
 * <li>{@link LocalDate}
 * <li>{@link LocalTime}
 * <li>{@link LocalDateTime}
 * <li>{@link Boolean}
 * <li>{@link Character}
 * <li>{@link String}
 * </ul>
 * As well as {@link Array} and {@link List} of the above mentioned types.
 * Primitives aren't allowed, use their object representation instead
 * <hr>
 * <p>These {@link Param}s can be annotated with 3 types of {@link Annotation}s
 * and 1 additional one for {@link Model} type of {@link Param}s only,
 * these {@link Annotation}s are:
 * <ul>
 * <li>{@link Name}
 * <li>{@link Validator} type {@link Annotation}
 * <li>{@link Consumer} type {@link Annotation}
 * <li>{@link Composed} (for {@link Model}s only)
 * </ul>
 * These {@link Annotation}s perform checks and operations on the 
 * {@link Param}s.
 * <p>You can read
 * about each one of them in their respective documentation (and you should, before
 * using some of them as that they have special behavior),
 * but in <i>short</i>:
 * <ul>
 * <li>{@link Name} allows you to specify the name of the incoming parameter,
 * but by default it is the same name as that of the {@link Parameter}
 * <li>{@link Validator}s make sure that the incoming parameter
 * conforms to a certain condition
 * <li>{@link Consumer}s give new values to the parameter or/and perform
 * certain actions on them
 * <li>{@link Composed} automatically fills the {@link Field}s of a {@link Model}
 * from the incoming parameters
 * </ul>
 * <p>An example of a {@link Validator} type {@link Annotation} would be
 * the {@link Required} {@link Annotation}, which asserts that this {@link Param}
 * should exists with the incoming parameters and must be cast-able to its type.
 * <p>As for {@link Consumer} type {@link Annotation}, an example would be
 * the {@link Default} {@link Annotation} which gives a defined
 * value to the {@link Param} if there was no corresponding parameter
 * in the incoming request
 * <p><b>Know that:</b> the {@link Required}, {@link Default} and {@link Composed}
 * {@link Annotation}s are mutually exclusive
 * <p><b>Know that:</b> the {@link Required} {@link Validator} feature
 * is set on by default on all {@link Param}s, except of course
 * if they are annotated with the {@link Default} or {@link Composed} {@link Annotation}
 * <p>Not all {@link Validator}s go on all type of {@link Param}s, and
 * same for {@link Consumer}s. For example you can't have the {@link UpperCase}
 * {@link Consumer} on an {@link Integer} type of {@link Param}..
 * <hr>
 * <p>Any {@link Model} that you want to have as a {@link Param} need to satisfy
 * these conditions:
 * <ul>
 * <li>The ID field should be annotated with the {@link MID} {@link Annotation}
 * <li>The ID field can only be one of these types:
 * 		<ul>
 * 		<li>{@link Number} ({@link Integer}, {@link Float}, {@link Double}, {@link Long}, {@link Short}, {@link Byte})
 *		<li>{@link Enum}
 * 		<li>{@link String}
 * 		<li>{@link LocalDate}
 * 		<li>{@link LocalTime}
 * 		<li>{@link LocalDateTime}
 * 		<li>{@link Boolean}
 * 		<li>{@link Character}
 * 		</ul>
 * <i>Unfortunately</i> {@link UUID} is not supported. And so are primitives, use their
 * object representation instead
 * <li>Have a public zero-args {@link Constructor}. Preferably one that won't
 * throw any {@link Exception}s, because if it does {@link jeext} won't handle
 * them, duh. (Know that by default, if no constructor was explicitly declared, a
 * public zero-args one is automatically added)
 * </ul>
 * <hr>
 * @implNote
 * <p>The following part only discusses how the {@link Param} class is
 * conceptualized, and
 * there is no need to read it unless you intend to modify some of the code
 * <p>There is a distinction between {@link Param}s that are {@link Model}s
 * and annotated with the {@link Composed} {@link Annotation}, they
 * are the {@link Nature#COMPOSED} {@link Param}s. And the rest of
 * the {@link Param}s, which are the {@link Nature#RETRIEVED} {@link Param}s
 * <p>Any {@link Param} is retrieved using the {@link #retriever}, except if
 * its {@link Nature#COMPOSED} then its retrieved with the {@link #composer}. Which
 * in turn uses a bunch of {@link Retriever}s (You can read more about it
 * in its own documentation)
 * <p>Most of the work and heavy lifting is done by the {@link Retriever}
 * <p>There are currently no {@link Validator}s or {@link Consumer}s that go
 * with the {@link Nature#COMPOSED} {@link Param}s so the check and operations
 * are only done to {@link Nature#RETRIEVED} {@link Param}s
 */
public class Param {
	
	/**
	 * An immutable {@link Set} of all the existing {@link Validator}s
	 */
	public static final Set <Class <? extends Annotation>> ALL_VALIDATORS = Set.of(After.class, Alphabetic.class, Alphanumeric.class, Before.class, Email.class, Max.class, Min.class, NonBlank.class, Older.class, Regex.class, Required.class, Younger.class);
	/**
	 * An immutable {@link Set} of all the existing {@link Consumer}s
	 */
	public static final Set <Class <? extends Annotation>> ALL_CONSUMERS = Set.of(Capitalize.class, Default.class, LowerCase.class, UpperCase.class);

	/**
	 * The nature of this {@link Param},
	 * either {@link Nature#RETRIEVED}
	 * or {@link Nature#COMPOSED}
	 */
	private Nature nature;
	/**
	 * If this {@link Param} is
	 * {@link Nature#RETRIEVED} then
	 * there is an instance of a {@link Retriever}
	 * for this {@link Param} in {@link #retriever}
	 * and <code>null</code> in {@link #composer}
	 */
	private Retriever retriever;
	/**
	 * If this {@link Param} is
	 * {@link Nature#COMPOSED} then
	 * there is an instance of a {@link Composer}
	 * for this {@link Param} in {@link #composer}
	 * and <code>null</code> in {@link #retriever}
	 */
	private Composer composer;
	
	/**
	 * <p>An {@link Array} of all the
	 * {@link Validator}s that this {@link Param}
	 * has.
	 * <p>Currently there is no {@link Validator}
	 * that can go on {@link Nature#COMPOSED}
	 * {@link Param}s
	 */
	private Validator [] validators;
	/**
	 * <p>An {@link Array} of all the
	 * {@link Consumer}s that this {@link Param}
	 * has.
	 * <p>Currently there is no {@link Consumer}
	 * that can go on {@link Nature#COMPOSED}
	 * {@link Param}s
	 */
	private Consumer [] consumers;
	
	/**
	 * <p>The {@link Constructor} that initializes the {@link Param}
	 * according to its {@link Nature}, and checks whether everything
	 * is as it should be, and there is no wrong {@link Annotation}s
	 * and so on
	 * <p>Its called by 
	 * {@link Mapping#Mapping(Class, Method, jeext.controller.core.Access, models.permission.Permission[], Boolean)}
	 * 
	 * @param webController
	 * @param method
	 * @param parameter
	 */
	public Param (Class <?> webController, Method method, Parameter parameter) {
		try {
			if (parameter.isAnnotationPresent(Composed.class)) {
				nature = Nature.COMPOSED;
				composer = new Composer(parameter);
				
			} else {
				nature = Nature.RETRIEVED;
				retriever = new Retriever(parameter);
			}
		} catch (FailedParamInit e) {
			throw new InvalidMappingMethodParam(webController, method, parameter, e.reason);
		}
		
		processAnnotations(webController, method, parameter);
	}
	
	/**
	 * Processes the {@link Annotation}s present
	 * on this {@link Param}, makes sure there is no conflicting {@link Annotation}s
	 * and adds the {@link Validator}s and {@link Consumer}s that are going
	 * to be used by this {@link Param}
	 * 
	 * @param webController
	 * @param method
	 * @param parameter
	 */
	private void processAnnotations (Class <?> webController, Method method, Parameter parameter) {
		int count = 0;
		
		count += parameter.isAnnotationPresent(Default.class)? 1 : 0;
		count += parameter.isAnnotationPresent(Composed.class)? 1 : 0;
		count += (parameter.isAnnotationPresent(Required.class) &&
				parameter.getAnnotation(Required.class).value())? 1 : 0;
		
		if (count > 1) {
			throw new InvalidMappingMethodParam(webController, method, parameter, "The `" +Required.class +"`, `" +Default.class +"` and `" +Composed.class +"` annotations are mutually exclusive");
		}
		
		processValidators(webController, method, parameter);
		processConsumers(webController, method, parameter);
	}

	/**
	 * Processes all the {@link Validator} {@link Annotation}s
	 * that exists on this {@link Param} and makes sure
	 * there are correctly used
	 * 
	 * @param webController
	 * @param method
	 * @param parameter
	 */
	private void processValidators (Class <?> webController, Method method, Parameter parameter) {
		if (nature == Nature.COMPOSED) {
			checkAnnotations(webController, method, parameter, ALL_VALIDATORS);
			validators = new Validator [0];
			return;
		}
		
		List <Validator> _validators = new ArrayList <> ();
		
		/**
		 * No need to check if Composed is not present, because
		 * if it is composed the method is short circuited
		 * from the get go
		 */
		if (!parameter.isAnnotationPresent(Default.class) &&
				(!parameter.isAnnotationPresent(Required.class) || parameter.getAnnotation(Required.class).value())) {
			_validators.add(RequiredValidator.GET());
		}
		
		switch (retriever.multiplicity) {
		case SINGLE:
			
		{
			if (String.class.equals(retriever.type)) {

				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, NonBlank.class, Min.class, Max.class, Regex.class, Email.class, Alphabetic.class, Alphanumeric.class);

				if (parameter.getAnnotation(NonBlank.class) != null) {
					_validators.add(NonBlankValidator.GET());
				}

				Min min = parameter.getAnnotation(Min.class);
				if (min != null) {
					int value = (int) min.value();
					if (value != min.value() || value < 0) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, min, "Must be a positive or null integer value");
					}
					
					_validators.add(MinStringValidator.GET(value, min.strict()));
				}
				
				Max max = parameter.getAnnotation(Max.class);
				if (max != null) {
					int value = (int) max.value();
					if (value != max.value() || value < 0) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
					}
					
					_validators.add(MaxStringValidator.GET(value, max.strict()));
				}
				
				Regex regex = parameter.getAnnotation(Regex.class);
				if (regex != null) {
					try {
						Pattern.compile(regex.value()); // Not efficient to compile here and there in the validator, I know
					} catch (PatternSyntaxException e) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, regex, "Can't compile the regex");
					}
					
					_validators.add(RegexValidator.GET(regex.value()));
				}
				
				if (parameter.isAnnotationPresent(Email.class)) {
					_validators.add(RegexValidator.GET("^([a-zA-Z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,5})$"));
				}
				
				if (parameter.isAnnotationPresent(Alphabetic.class)) {
					if (parameter.getAnnotation(Alphabetic.class).allowUnderscore()) {
						_validators.add(RegexValidator.GET("^[a-zA-Z_]+$"));
					} else {
						_validators.add(RegexValidator.GET("^[a-zA-Z]+$"));
					}
				}
				
				if (parameter.isAnnotationPresent(Alphanumeric.class)) {
					if (parameter.getAnnotation(Alphanumeric.class).allowUnderscore()) {
						_validators.add(RegexValidator.GET("^[a-zA-Z0-9_]+$"));
					} else {
						_validators.add(RegexValidator.GET("^[a-zA-Z0-9]+$"));
					}
				}
				
			} else if (Number.class.isAssignableFrom(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Min.class, Max.class);
				
				Min min = parameter.getAnnotation(Min.class);
				if (min != null) {
					_validators.add(MinValidator.GET(min.value(), min.strict()));
				}
				
				Max max = parameter.getAnnotation(Max.class);
				if (max != null) {
					_validators.add(MaxValidator.GET(max.value(), max.strict()));
				}
				
			} else if (Model.class.isAssignableFrom(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else if (LocalDate.class.equals(retriever.type)) {

				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Before.class, After.class, Younger.class, Older.class);
				
				Before before = parameter.getAnnotation(Before.class);
				if (before != null) {
					LocalDate value = Parser.parseDate(before.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, before, "Must be a valid LocalDate");
					}
					
					_validators.add(BeforeDateValidator.GET(value));
				}
				
				After after = parameter.getAnnotation(After.class);
				if (after != null) {
					LocalDate value = Parser.parseDate(after.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, after, "Must be a valid LocalDate");
					}
					
					_validators.add(AfterDateValidator.GET(value));
				}

				Younger younger = parameter.getAnnotation(Younger.class);
				if (younger != null) {
					PeriodHolder value = PeriodHolder.parseOrNull(younger.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, younger, "Must be a valid PeriodHolder representation");
					}
					
					_validators.add(YoungerValidator.GET(value));
				}

				Older older = parameter.getAnnotation(Older.class);
				if (older != null) {
					PeriodHolder value= PeriodHolder.parse(older.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, older, "Must be a valid PeriodHolder representation");
					}
					
					_validators.add(OlderValidator.GET(value));
				}
				
			} else if (Boolean.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else if (Character.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else if (LocalTime.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Before.class, After.class);
				
				Before before = parameter.getAnnotation(Before.class);
				if (before != null) {
					LocalTime value = Parser.parseTime(before.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, before, "Must be a valid LocalTime");
					}
					
					_validators.add(BeforeTimeValidator.GET(value));
				}
				
				After after = parameter.getAnnotation(After.class);
				if (after != null) {
					LocalTime value = Parser.parseTime(after.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, after, "Must be a valid LocalTime");
					}
					
					_validators.add(AfterTimeValidator.GET(value));
				}
				
			} else if (LocalDateTime.class.equals(retriever.type)) {	
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Before.class, After.class);
				
				Before before = parameter.getAnnotation(Before.class);
				if (before != null) {
					LocalDateTime value = Parser.parseDateTime(before.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, before, "Must be a valid LocalDateTime");
					}
					
					_validators.add(BeforeDateTimeValidator.GET(value));
				}
				
				After after = parameter.getAnnotation(After.class);
				if (after != null) {
					LocalDateTime value = Parser.parseDateTime(after.value());
					if (value == null) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, after, "Must be a valid LocalDateTime");
					}
					
					_validators.add(AfterDateTimeValidator.GET(value));
				}
			
			} else if (Enum.class.isAssignableFrom(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
			
			} else if (FileType.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Max.class, Min.class);
				
				Min min = parameter.getAnnotation(Min.class);
				if (min != null) {
					long value = (long) min.value();
					if (value != min.value() || value < 0) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, min, "Must be a positive or null integer value");
					}
					
					_validators.add(MinFileValidator.GET(value, min.strict()));
				}
				
				Max max = parameter.getAnnotation(Max.class);
				if (max != null) {
					long value = (long) max.value();
					if (value != max.value() || value < 0) {
						throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
					}
					
					_validators.add(MaxFileValidator.GET(value, max.strict()));
				}
				
			} else {
				throw new UnsupportedType(retriever.type);
			}
		}
			
			break;
		case ARRAY:
			
		{
			checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Min.class, Max.class);
			
			Min min = parameter.getAnnotation(Min.class);
			if (min != null) {
				int value = (int) min.value();
				if (value != min.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, min, "Must be a positive or null integer value");
				}
				
				_validators.add(MinArrayValidator.GET(retriever.type, value, min.strict()));
			}
			
			Max max = parameter.getAnnotation(Max.class);
			if (max != null) {
				int value = (int) max.value();
				if (value != max.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
				}
				
				_validators.add(MaxArrayValidator.GET(retriever.type, value, max.strict()));
			}
		}
		
			break;
		case LIST:
			
		{
			checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Min.class, Max.class);
			
			Min min = parameter.getAnnotation(Min.class);
			if (min != null) {
				int value = (int) min.value();
				if (value != min.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, min, "Must be a positive or null integer value");
				}
				
				_validators.add(MinListValidator.GET(retriever.type, value, min.strict()));
			}
			
			Max max = parameter.getAnnotation(Max.class);
			if (max != null) {
				int value = (int) max.value();
				if (value != max.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
				}
				
				_validators.add(MaxListValidator.GET(retriever.type, value, max.strict()));
			}
		}
		
			break;
		default:
			throw new UnsupportedType(retriever.multiplicity);
		}
		
		this.validators = _validators.toArray(new Validator [_validators.size()]);
	}
	
	/**
	 * Processes all the {@link Consumer} {@link Annotation}s
	 * that exists on this {@link Param} and makes sure
	 * there are correctly used
	 * 
	 * @param webController
	 * @param method
	 * @param parameter
	 */
	private void processConsumers (Class <?> webController, Method method, Parameter parameter) {
		if (nature == Nature.COMPOSED) {
			checkAnnotations(webController, method, parameter, ALL_CONSUMERS);
			consumers = new Consumer [0];
			return;
		}
		
		List <Consumer> _consumers = new ArrayList <> ();
		
		switch (retriever.multiplicity) {
		case SINGLE:
			
		{
			if (String.class.equals(retriever.type)) {

				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class, Capitalize.class, UpperCase.class, LowerCase.class);

				if (parameter.isAnnotationPresent(Default.class)) {
					_consumers.add(DefaultConsumer.GET(parameter.getAnnotation(Default.class).value()));
				}
				
				if (parameter.isAnnotationPresent(Capitalize.class)) {
					_consumers.add(CapitalizeConsumer.GET(parameter.getAnnotation(Capitalize.class).forceCapitalize()));
				}
				
				if (parameter.isAnnotationPresent(UpperCase.class)) {
					_consumers.add(UpperCaseConsumer.GET());
				}
				
				if (parameter.isAnnotationPresent(LowerCase.class)) {
					_consumers.add(LowerCaseConsumer.GET());
				}
				
			} else if (Number.class.isAssignableFrom(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);

				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Number value = (Number) Retriever.parse(_default.value(), retriever.type);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a Number that is compatible with the type");
					}
					
					_consumers.add(DefaultConsumer.GET(value));
				}
				
			} else if (Model.class.isAssignableFrom(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					
					Object value;
					switch (retriever.idKind) {
					case PRIMITIVE:
						value = Retriever.parse(_default.value(), retriever.idType);
						break;
					case ENUM:
						value = Retriever.parseEnum(_default.value(), retriever.idType);
						break;
					default:
						throw new UnsupportedType(retriever.idKind);
					}
					
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a value that can be parsed to the models id type. The value is `" +_default.value() +"` yet the id type is `" +retriever.idType +"`");
					}
					
					_consumers.add(DefaultModelConsumer.GET(retriever.instance, value));
				}
				
			} else if (LocalDate.class.equals(retriever.type)) {

				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					LocalDate value = Retriever.parse(_default.value(), LocalDate.class);
					if (value == null) {
						if (_default.value().equalsIgnoreCase("now")) {
							_consumers.add(DefaultNowDateConsumer.GET());
						} else {
							throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a valid LocalDate or `now`");
						}
					} else {
						_consumers.add(DefaultConsumer.GET(value));
					}
				}
				
			} else if (Boolean.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Boolean value = Retriever.parse(_default.value(), Boolean.class);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a valid Boolean value (TRUE/false, Y/n , 1/0..)");
					}
					
					_consumers.add(DefaultConsumer.GET(value));
				}
				
			} else if (Character.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class, UpperCase.class, LowerCase.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Character value = Retriever.parse(_default.value(), Character.class);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a single character");
					}
					_consumers.add(DefaultConsumer.GET(value));
				}
				
				if (parameter.isAnnotationPresent(UpperCase.class)) {
					_consumers.add(UpperCaseCharConsumer.GET());
				}
				
				if (parameter.isAnnotationPresent(LowerCase.class)) {
					_consumers.add(LowerCaseCharConsumer.GET());
				}
				
			} else if (LocalTime.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);

				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					LocalTime value = Retriever.parse(_default.value(), LocalTime.class);
					if (value == null) {
						if (_default.value().equalsIgnoreCase("now")) {
							_consumers.add(DefaultNowTimeConsumer.GET());
						} else {
							throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a valid LocalTime or `now`");
						}
					} else {
						_consumers.add(DefaultConsumer.GET(value));
					}
				}	
				
			} else if (LocalDateTime.class.equals(retriever.type)) {	
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);

				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					LocalDateTime value = Retriever.parse(_default.value(), LocalDateTime.class);
					if (value == null) {
						if (_default.value().equalsIgnoreCase("now")) {
							_consumers.add(DefaultNowDateTimeConsumer.GET());
						} else {
							throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a valid LocalDateTime or `now`");
						}
					} else {
						_consumers.add(DefaultConsumer.GET(value));
					}
				}				
			
			} else if (Enum.class.isAssignableFrom(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Object value = Retriever.parseEnum(_default.value(), retriever.type);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be one of the Enums constants (case sensitive)");
					}

					_consumers.add(DefaultConsumer.GET(value));
				}
		
			} else if (FileType.class.equals(retriever.type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS);
				
			} else {
				throw new UnsupportedType(retriever.type);
			}
		}
			
			break;
		case ARRAY:
			
		{
			checkAnnotations(webController, method, parameter, ALL_CONSUMERS);
		}
		
			break;
		case LIST:
			
		{
			checkAnnotations(webController, method, parameter, ALL_CONSUMERS);
		}
		
			break;
		default:
			throw new UnsupportedType(retriever.multiplicity);
		}
		
		this.consumers = _consumers.toArray(new Consumer [_consumers.size()]);
	}
	
	/**
	 * <p>Used by
	 * {@link #processValidators(Class, Method, Parameter)}
	 * and
	 * {@link #processConsumers(Class, Method, Parameter)}
	 * to check if there are no conflicting {@link Annotation}s
	 * 
	 * @param webController
	 * @param method
	 * @param parameter
	 * @param all
	 * @param possible
	 */
	@SafeVarargs
	private static void checkAnnotations (Class <?> webController, Method method, Parameter parameter, Set <Class <? extends Annotation>> all, Class <? extends Annotation> ... possible) {
		for (Annotation annotation : parameter.getDeclaredAnnotations()) {
			Class <? extends Annotation> annotationClass = annotation.annotationType();
			
			if (all.contains(annotationClass) && !Arrays.stream(possible).anyMatch(annotationClass::equals)) {
				throw new InvalidMappingMethodParam(webController, method, parameter, "Cannot use this annotation `" +annotation +"` on that type of parameter");
			}
		}
	}
	
	/**
	 * The method that gets called by
	 * {@link Mapping#invoke(HttpServletRequest, HttpServletResponse)}
	 * to retrieve all the {@link Param}s
	 * 
	 * @param request
	 * @throws InvalidParameter	If one of the {@link Validator}s fails
	 * @throws InvocationTargetException If one of the setter methods
	 * used by the {@link Composer} throws an {@link Exception}
	 */
	public Object getParam (HttpServletRequest request) throws InvalidParameter, InvocationTargetException {
		try {
			Object value;
			
			switch (nature) {
			case RETRIEVED:
				value = retriever.retrieve(request);
				
				validate(value);
				value = consume(value);
				
				break;
				
			case COMPOSED:
				value = composer.compose(request);
				break;
				
			default:
				throw new UnsupportedType(nature);
			}
			
			return value;
			
		} catch (InvocationTargetException | InvalidParameter e) {
			throw e;
		} catch (ClassCastException e) {
			throw new UnhandledJeextException(e);
		}
	}
	
	/**
	 * Goes over all the {@link Validator}s
	 * this {@link Param} has, and {@link Validator#validate(Object)}s the
	 * value of the parameter
	 * 
	 * @param value
	 * @throws InvalidParameter	If one of them fails
	 */
	private void validate (Object value) throws InvalidParameter {
		for (int i = 0; i < validators.length; i++) {
			if (!validators[i].validate(value)) {
				throw new InvalidParameter(this, "Failed this validator `" +validators[i] +"`");
			}
		}
	}
	
	/**
	 * Goes over all the {@link Consumer}s
	 * this {@link Param} has, and {@link Consumer#consume(Object)}s
	 * the value of the parameter
	 * 
	 * @param value
	 */
	private Object consume (Object value) {
		for (int i = 0; i < consumers.length; i++) {
			value = consumers[i].consume(value);
		}
		
		return value;
	}
	
	/**
	 * {@link Enum} to specify
	 * which type of {@link Param}
	 * this {@link Param} is
	 */
	private static enum Nature {
		RETRIEVED,
		COMPOSED;
	}
	
	/**
	 * Used by {@link Retriever} and {@link Composer}
	 * to indicate that requirement failed, and then
	 * its up to the {@link Param} to signal it to
	 * the user
	 */
	protected static class FailedParamInit extends Exception {
		private static final long serialVersionUID = 1L;
		
		protected final String reason;
		
		protected FailedParamInit (String reason) {
			this.reason = reason;
		}
	}
	
}
