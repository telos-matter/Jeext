package jeext.controller.core.param;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParam;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamConsumer;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamValidator;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.composer.annotations.ModelId;
import jeext.controller.core.param.consumers.*;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.validators.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.dao.Manager;
import jeext.model.Model;
import jeext.util.Dates.PeriodHolder;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledDevException;
import jeext.util.exceptions.UnsupportedType;
import jeext.util.Parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

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
	
	private static final Set <Class <? extends Annotation>> ALL_VALIDATORS = Set.of(After.class, Alphabetic.class, Alphanumeric.class, Before.class, Email.class, Max.class, Min.class, NonBlank.class, Older.class, Regex.class, Required.class, Younger.class);
	private static final Set <Class <? extends Annotation>> ALL_CONSUMERS = Set.of(Capitalize.class, Default.class, LowerCase.class, UpperCase.class);

	private Class <?> type;
	private Multiplicity multiplicity; 
	/**
	 * Primitive here means anything other than a Model.
	 * Primitives themselves (int, float..) are not allowed
	 */
	private Kind kind;
	
	private Class <?> idType;
	private IdKind idKind;
	
	private String name;
	private Validator [] validators;
	private Consumer [] consumers;
	
	public static void main(String[] args) {
//		Class<?> c = Multiplicity.class;
//		Arrays.stream(c.getEnumConstants()).forEach(System.out::println);
//		System.out.println(c.getEnumConstants()[0] == Multiplicity.SINGLE);
//		System.out.println(getEnum(null, Multiplicity.class, null));
	}
	
	public Param (Class <?> webController, Method method, Parameter parameter) {
		type = parameter.getType();
		
		if (type.isArray()) {
			type = type.componentType();
			multiplicity = Multiplicity.ARRAY;
			
		} else if (List.class.isAssignableFrom(type)) {
			ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
			type = (Class <?>) parameterizedType.getActualTypeArguments()[0];
			multiplicity = Multiplicity.LIST;
			
		} else {
			multiplicity = Multiplicity.SINGLE;
		}
		
		if (type.isPrimitive()) {
			throw new InvalidMappingMethodParam(webController, method, parameter, "Primitives aren't allowed, use their Object representation instead");
		}
		
		if (!validType(type)) {
			throw new InvalidMappingMethodParam(webController, method, parameter, "Unsuported type `" +type +"`");
		}

		
		if (Model.class.isAssignableFrom(type)) {
			kind = Kind.MODEL;
		} else if (Enum.class.isAssignableFrom(type)) {
			kind = Kind.ENUM;
		} else {
			kind = Kind.PRIMITIVE;
		}
		
		if (kind == Kind.MODEL) {
			Field [] fields = type.getDeclaredFields();
			
			int count = (int) Arrays
					.stream(fields)
					.filter((Field field) -> {return field.isAnnotationPresent(ModelId.class);})
					.count();
			if (count != 1) {
				throw new InvalidMappingMethodParam(webController, method, parameter, "The Model has to indentify 1 ID field with the `" +ModelId.class.getSimpleName() +"` annotation");
			}
			
			Field id = Arrays
					.stream(fields)
					.filter((Field field) -> {return field.isAnnotationPresent(ModelId.class);})
					.toList()
					.get(0);
			
			idType = id.getType();
			if (!	(String.class.equals(idType) ||
					Number.class.isAssignableFrom(idType) ||
					Enum.class.isAssignableFrom(idType) ||
					LocalDate.class.equals(idType) ||
					Boolean.class.equals(idType) ||
					Character.class.equals(idType) ||
					LocalTime.class.equals(idType) ||
					LocalDateTime.class.equals(idType))
				||	(Number.class.equals(idType) ||
					Enum.class.equals(idType))) {
				throw new InvalidMappingMethodParam(webController, method, parameter, "Can't use this Model that has this type of ID `" +idType +"` since it's not supported as an ID type.");
			}
			
			if (Enum.class.isAssignableFrom(idType)) {
				idKind = IdKind.ENUM;
			} else {
				idKind = IdKind.PRIMITIVE;
			}
		}
		
		name = (parameter.isAnnotationPresent(Name.class))? parameter.getAnnotation(Name.class).value() : parameter.getName();

		
		processAnnotations(webController, method, parameter);
	}
	
	public static boolean validType (Class <?> type) {
		Objects.requireNonNull(type);
		
		if (!	(String.class.equals(type) ||
				Number.class.isAssignableFrom(type) ||
				Model.class.isAssignableFrom(type) ||
				Enum.class.isAssignableFrom(type) ||
				LocalDate.class.equals(type) ||
				Boolean.class.equals(type) ||
				Character.class.equals(type) ||
				LocalTime.class.equals(type) ||
				LocalDateTime.class.equals(type))
			||	(Number.class.equals(type) ||
				Model.class.equals(type) ||
				Enum.class.equals(type))) {
			return false;
		} else {
			return true;
		}
	}
	
	private void processAnnotations (Class <?> webController, Method method, Parameter parameter) {
		if (parameter.isAnnotationPresent(Required.class) &&
				parameter.getAnnotation(Required.class).value() &&
				parameter.isAnnotationPresent(Default.class)) {
			throw new InvalidMappingMethodParam(webController, method, parameter, "Required and Default are mutually exclusive");
		}
		
		processValidators(webController, method, parameter);
		processConsumers(webController, method, parameter);
	}

	private void processValidators (Class <?> webController, Method method, Parameter parameter) {
		List <Validator> _validators = new ArrayList <> ();
		
		if (!parameter.isAnnotationPresent(Default.class) &&
				(!parameter.isAnnotationPresent(Required.class) || parameter.getAnnotation(Required.class).value())) {
			_validators.add(RequiredValidator.GET());
		}
		
		switch (multiplicity) {
		case SINGLE:
			
			if (String.class.equals(type)) {

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
						Pattern.compile(regex.value()); // Not efficient to compile here and there in the validator, I know.
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
				
			} else if (Number.class.isAssignableFrom(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class, Min.class, Max.class);
				
				Min min = parameter.getAnnotation(Min.class);
				if (min != null) {
					_validators.add(MinValidator.GET(min.value(), min.strict()));
				}
				
				Max max = parameter.getAnnotation(Max.class);
				if (max != null) {
					_validators.add(MaxValidator.GET(max.value(), max.strict()));
				}
				
			} else if (Model.class.isAssignableFrom(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else if (LocalDate.class.equals(type)) {

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
				
			} else if (Boolean.class.equals(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else if (Character.class.equals(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else if (LocalTime.class.equals(type)) {
				
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
				
			} else if (LocalDateTime.class.equals(type)) {	
				
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
			
			} else if (Enum.class.isAssignableFrom(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_VALIDATORS, Required.class);
				
			} else {
				throw new UnsupportedType(type);
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
				
				_validators.add(MinArrayValidator.GET(type, value, min.strict()));
			}
			
			Max max = parameter.getAnnotation(Max.class);
			if (max != null) {
				int value = (int) max.value();
				if (value != max.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
				}
				
				_validators.add(MaxArrayValidator.GET(type, value, max.strict()));
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
				
				_validators.add(MinListValidator.GET(type, value, min.strict()));
			}
			
			Max max = parameter.getAnnotation(Max.class);
			if (max != null) {
				int value = (int) max.value();
				if (value != max.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
				}
				
				_validators.add(MaxListValidator.GET(type, value, max.strict()));
			}
		}
		
			break;
		default:
			throw new UnsupportedType(multiplicity);
		}
		
		this.validators = _validators.toArray(new Validator [_validators.size()]);
	}
	
	private void processConsumers (Class <?> webController, Method method, Parameter parameter) {
		List <Consumer> _consumers = new ArrayList <> ();
		
		switch (multiplicity) {
		case SINGLE:
			
			if (String.class.equals(type)) {

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
				
			} else if (Number.class.isAssignableFrom(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);

				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Number value = (Number) parse(_default.value(), type);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a Number that is compatible with the type");
					}
					
					_consumers.add(DefaultConsumer.GET(value));
				}
				
			} else if (Model.class.isAssignableFrom(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Long value = parse(_default.value(), Long.class);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be same type as the models id"); // TODO
					}
					
					_consumers.add(DefaultModelConsumer.GET(type, value));
				}
				
			} else if (LocalDate.class.equals(type)) {

				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					LocalDate value = parse(_default.value(), LocalDate.class);
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
				
			} else if (Boolean.class.equals(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Boolean value = parse(_default.value(), Boolean.class);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a valid Boolean value (TRUE/false, Y/n , 1/0..)");
					}
					
					_consumers.add(DefaultConsumer.GET(value));
				}
				
			} else if (Character.class.equals(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class, UpperCase.class, LowerCase.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Character value = parse(_default.value(), Character.class);
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
				
			} else if (LocalTime.class.equals(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);

				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					LocalTime value = parse(_default.value(), LocalTime.class);
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
				
			} else if (LocalDateTime.class.equals(type)) {	
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);

				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					LocalDateTime value = parse(_default.value(), LocalDateTime.class);
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
			
			} else if (Enum.class.isAssignableFrom(type)) {
				
				checkAnnotations(webController, method, parameter, ALL_CONSUMERS, Default.class);
				
				Default _default = parameter.getAnnotation(Default.class);
				if (_default != null) {
					Object value = parseEnum(_default.value(), type);
					if (value == null) {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be one of the Enums constants (case sensitive)");
					}

					_consumers.add(DefaultConsumer.GET(value));
				}
				
			} else {
				throw new UnsupportedType(type);
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
			throw new UnsupportedType(multiplicity);
		}
		
		this.consumers = _consumers.toArray(new Consumer [_consumers.size()]);
	}
	
	@SafeVarargs
	private static void checkAnnotations (Class <?> webController, Method method, Parameter parameter, Set <Class <? extends Annotation>> all, Class <? extends Annotation> ... possible) {
		for (Annotation annotation : parameter.getAnnotations()) {
			Class <? extends Annotation> annotationClass = annotation.annotationType();
			
			if (all.contains(annotationClass) && !Arrays.stream(possible).anyMatch(annotationClass::equals)) {
				throw new InvalidMappingMethodParam(webController, method, parameter, "Cannot use this annotation `" +annotation +"` on that type of parameter");
			}
		}
	}
	
	public Object getParam (HttpServletRequest request) throws InvalidParameter {
		try {
			Object value;
			
			switch (multiplicity) { // Yield woud've been nice here, but there is no syntax coloring for it in Eclipse, so no. lol
			case SINGLE:
				
				switch (kind) {
				case PRIMITIVE:
					value = getParameter(name, type, request);
					break;
				case ENUM:
					value = getEnum(name, type, request);
					break;
				case MODEL:
					value = getEntity(name, type, idType, idKind, request);
					break;
				default:
					throw new UnsupportedType(kind);
				}
				
				break;
			case ARRAY:
				
				switch (kind) {
				case PRIMITIVE:
					value = getParameters(name, type, request);
					break;
				case ENUM:
					value = getEnums(name, type, request);
					break;
				case MODEL:
					value = getEntities(name, type, idType, idKind, request);
					break;
				default:
					throw new UnsupportedType(kind);
				}

				break;
			case LIST:
				
				switch (kind) {
				case PRIMITIVE:
					value = getParameters(name, type, request);
					break;
				case ENUM:
					value = getEnums(name, type, request);
					break;
				case MODEL:
					value = getEntities(name, type, idType, idKind, request);
					break;
				default:
					throw new UnsupportedType(kind);
				}
				value = constructList(value, type);
				
				break;
			default:
				throw new UnsupportedType(multiplicity);
			}
			
			validate(value);
			return consume(value);
			
		} catch (InvalidParameter e) {
			throw e;
		} catch (Exception e) { // In case I missed something with all the casting going on
			throw new UnhandledDevException(e);
		}
	}
	
	private void validate (Object value) throws InvalidParameter {
		for (Validator validator : validators) {
			if (!validator.validate(value)) {
				throw new InvalidParameter(this, validator);
			}
		}
	}
	
	private Object consume (Object value) {
		for (Consumer consumer : consumers) {
			value = consumer.consume(value);
		}
		
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List <T> constructList (Object array, Class <T> type) {
		if (array == null) {
			return null;
		}
		
		return Arrays.asList((T []) array);
	}
	
	private static <T, S> T getEntity (String name, Class <T> type, Class <S> idType, IdKind idKind, HttpServletRequest request) {
		S id;
		switch (idKind) {
		case PRIMITIVE:
			id = getParameter(name, idType, request);
			break;
		case ENUM:
			id = getEnum(name, idType, request);
			break;
		default:
			throw new UnsupportedType(idType);
		}
		
		return parseModel(id, type);
	}
	
	@SuppressWarnings("unchecked")
	private static <T, S> T [] getEntities (String name, Class <T> type, Class <S> idType, IdKind idKind, HttpServletRequest request) {
		S [] ids;
		switch (idKind) {
		case PRIMITIVE:
			ids = (S []) getParameters(name, idType, request);
			break;
		case ENUM:
			ids = (S []) getEnums(name, idType, request);
			break;
		default:
			throw new UnsupportedType(idType);
		}
		
		if (ids == null) {
			return null;
		}
		
		T [] entities = (T []) Array.newInstance(type, ids.length);
		for (int i = 0; i < ids.length; i++) {
			entities[i] = parseModel(ids[i], type);
		}
		
		return entities;
	}
	
	private static <T> T getEnum (String name, Class <T> type, HttpServletRequest request) {
		String constant = getParameter(name, String.class, request);
		return (T) parseEnum(constant, type);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T [] getEnums (String name, Class <T> type, HttpServletRequest request) {
		String [] constants = getParameters(name, String.class, request);
		if (constants == null) {
			return null;
		}
		
		T [] enums = (T []) Array.newInstance(type, constants.length);
		for (int i = 0; i < enums.length; i++) {
			enums[i] = (T) parseEnum(constants[i], type);
		}
		return enums;
	}

	private static <T> T getParameter (String name, Class <T> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		return parse(parameter, type);
	}

	// keep in mind, if its not null then at least 1 param exists
	@SuppressWarnings("unchecked")
	private static <T> T [] getParameters (String name, Class <T> type, HttpServletRequest request) {
		String [] parameters = request.getParameterValues(name);
		if (parameters == null) {
			return null;
		}
		
		T [] parsedParameters = (T []) Array.newInstance(type, parameters.length);
		for (int i = 0; i < parameters.length; i++) {
			parsedParameters[i] = parse(parameters[i], type);
		}
		
		return parsedParameters;
	}
	
	private static <T> T parseModel (Object id, Class <T> type) {
		if (id == null) {
			return null;
		}
		
		return Manager.find(type, id);
	}
	
	@SuppressWarnings("unchecked")
	private static <T, S extends Enum <S>> T parseEnum (String constant, Class <T> type) {
		if (constant == null) {
			return null;
		}
		
		try {
			return (T) Enum.valueOf((Class <S>) type, constant);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (Exception e) {
			throw new UnhandledDevException(e);
		}
	}
	
	private static <T> T parse (String s, Class <T> type) {
		try {
			return Parser.parse(s, type);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new UnsupportedType(type);
		}
	}
	
	private static enum Multiplicity {
		SINGLE,
		ARRAY,
		LIST;
	}
	
	private static enum Kind {
		PRIMITIVE,
		ENUM,
		MODEL;
	}
	
	private static enum IdKind {
		PRIMITIVE,
		ENUM;
	}
	
}
