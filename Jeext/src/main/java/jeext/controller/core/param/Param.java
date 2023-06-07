package jeext.controller.core.param;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParam;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamConsumer;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamValidator;
import jeext.controller.core.param.Retriever.IDKind;
import jeext.controller.core.param.Retriever.Kind;
import jeext.controller.core.param.Retriever.Multiplicity;
import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.consumers.*;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.types.FileType;
import jeext.controller.core.param.validators.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.dao.Manager;
import jeext.model.Model;
import jeext.util.Dates.PeriodHolder;
import jeext.util.exceptions.UnhandledJeextException;
import jeext.util.exceptions.UnsupportedType;
import jeext.util.Parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>A {@link Param} is any {@link Parameter} in a
 * {@link Mapping} {@link Method} (except of course
 * {@link HttpServletRequest} and {@link HttpServletResponse})
 * that is expected as a parameter in the incoming HTTP request
 * <p>It provides an easy way to retrieve, check and operate on
 * the parameters that a user sends
 * <p>The allowed types are: 
 * <ul>
 * <li>{@link Model}
 * <li>{@link Number} ({@link Integer}, {@link Float}, {@link Double}, {@link Long}, {@link Short}, {@link Byte})
 * <li>{@link String}
 * <li>{@link Enum}
 * <li>{@link LocalDate}
 * <li>{@link LocalTime}
 * <li>{@link LocalDateTime}
 * <li>{@link Boolean}
 * <li>{@link Character}
 * </ul>
 * And {@link Array}s and {@link List}s of the above mentioned types.
 * Primitives aren't allowed, use their object representation instead
 * <p>These {@link Param}s can be annotated with 3 types of {@link Annotation}
 * and 1 additional one for {@link Model}s only, these {@link Annotation}s are:
 * <ul>
 * <li>{@link Name}
 * <li>{@link Validator} type {@link Annotation}
 * </ul>
 */
public class Param {
	
	// MENTION they are not commutative
	// MENTION by default required is on, but then if u use default its off automatically
	// MENTION only models integer based id are allowed, no error now, but error when we try to retrieve, no UUID chief
	// MENTION models should inherit from model to be considered a model, duh
	// MENTION default consumer for string "cannot" be empty string, cuz it consideres emptry string and null same
	// MENTION enums should we written as they are declared
	// MENTION idtype cant be primitive
	
	public static final Set <Class <? extends Annotation>> ALL_VALIDATORS = Set.of(After.class, Alphabetic.class, Alphanumeric.class, Before.class, Email.class, Max.class, Min.class, NonBlank.class, Older.class, Regex.class, Required.class, Younger.class);
	public static final Set <Class <? extends Annotation>> ALL_CONSUMERS = Set.of(Capitalize.class, Default.class, LowerCase.class, UpperCase.class);

	private Nature nature;
	private Retriever retriever;
	private Composer composer;
	
	private Validator [] validators;
	private Consumer [] consumers;
	
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
	// TODO do please go over the validators and consumers and check those that still use set if they are actually using hashable object
	
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
					Long value = Retriever.parse(_default.value(), Long.class);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be same type as the models id"); // TODO FIXME
					}
					
					_consumers.add(DefaultModelConsumer.GET(retriever.type, value));
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
	
	@SafeVarargs
	private static void checkAnnotations (Class <?> webController, Method method, Parameter parameter, Set <Class <? extends Annotation>> all, Class <? extends Annotation> ... possible) {
		for (Annotation annotation : parameter.getDeclaredAnnotations()) {
			Class <? extends Annotation> annotationClass = annotation.annotationType();
			
			if (all.contains(annotationClass) && !Arrays.stream(possible).anyMatch(annotationClass::equals)) {
				throw new InvalidMappingMethodParam(webController, method, parameter, "Cannot use this annotation `" +annotation +"` on that type of parameter");
			}
		}
	}
	
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
	
	private void validate (Object value) throws InvalidParameter {
		for (int i = 0; i < validators.length; i++) {
			if (!validators[i].validate(value)) {
				throw new InvalidParameter(this, "Failed this validator `" +validators[i] +"`");
			}
		}
	}
	
	private Object consume (Object value) {
		for (int i = 0; i < consumers.length; i++) {
			value = consumers[i].consume(value);
		}
		
		return value;
	}
	
	private static enum Nature {
		RETRIEVED,
		COMPOSED;
	}
	
	protected static class FailedParamInit extends Exception {
		private static final long serialVersionUID = 1L;
		
		protected final String reason;
		
		protected FailedParamInit (String reason) {
			this.reason = reason;
		}
	}
	
}
