package jeext.controller.core.param;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.mapping.Mapping;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParam;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamConsumer;
import jeext.controller.core.mapping.exceptions.InvalidMappingMethodParamValidator;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.consumers.*;
import jeext.controller.core.param.consumers.annotations.*;
import jeext.controller.core.param.validators.*;
import jeext.controller.core.param.validators.annotations.*;
import jeext.dao.Manager;
import jeext.models_core.Model;
import jeext.util.Dates.PeriodHolder;
import jeext.util.exceptions.UnsupportedType;
import jeext.util.Parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * <li>{@link Number}s ({@link Integer}, {@link Float}, {@link Double}, {@link Long}, {@link Short}, {@link Byte})
 * <li>{@link Boolean}
 * <li>{@link Character}
 * <li>{@link LocalDate}
 * </ul>
 * (Primitives aren't allowed, use their object representation instead)
 * <p>These {@link Param}s can be annotated with 3 types of {@link Annotation}
 * and 1 additional one for {@link Model}s only, these {@link Annotation}s are:
 * <ul>
 * <li>{@link Name}
 * <li>{@link Validator} type {@link Annotation}
 * </ul>
 */
public class Param {
	// TODO add composer
	private Class <?> type;
	/**
	 * Primitive here means anything other than a Model.
	 * Primitives themselves (int, float..) are not allowed
	 */
	private boolean primitive;
	private String name;
	private Validator [] validators;
	private Consumer [] consumers;
	// MENTION models should inherit from model to be considered a model, duh
	// TODO matches validator throws regex error
	// TODO default consumer for string cannot be empty string, cuz it consideres emptry string and null same
	// TODO: check periodholder, i removed returning null now its just exception
	@SuppressWarnings("unchecked")
	public Param(Class <?> webController, Method method, Parameter parameter) {
		
		this.type = parameter.getType();
		
		if (type.isPrimitive()) {
			throw new InvalidMappingMethodParam(webController, method, parameter, "Primitives aren't allowed, use their Object representation instead");
		}
		
		this.primitive = !Model.class.isAssignableFrom(type);
		
		this.name = (parameter.isAnnotationPresent(Name.class))? parameter.getAnnotation(Name.class).value() : parameter.getName();
		
		if (!(String.class.equals(type) ||
				Number.class.isAssignableFrom(type) ||
				Model.class.isAssignableFrom(type) ||
				LocalDate.class.equals(type) ||
				Boolean.class.equals(type) ||
				Character.class.equals(type))) {
			throw new InvalidMappingMethodParam(webController, method, parameter, "Unsuported type `" +type +"`");
		} 
		
		
		List <Validator> _validators = new ArrayList <> ();
		
		if (!parameter.isAnnotationPresent(Required.class) || parameter.getAnnotation(Required.class).value()) {
			_validators.add(RequiredValidator.GET());
		}
		
		if (String.class.equals(type)) {

			if (parameter.getAnnotation(NonBlank.class) != null) {
				_validators.add(NonBlankValidator.GET());
			}

			Min min = parameter.getAnnotation(Min.class);
			if (min != null) {
				int value = (int) min.value();
				if (value != min.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, min, "Must be a positive or null integer value");
				}
				
				if (min.strict()) {
					_validators.add(MinStrictStringValidator.GET(value));
				} else {
					_validators.add(MinStringValidator.GET(value));
				}
			}
			
			Max max = parameter.getAnnotation(Max.class);
			if (max != null) {
				int value = (int) max.value();
				if (value != max.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, max, "Must be a positive or null integer value");
				}
				
				if (max.strict()) {
					_validators.add(MaxStrictStringValidator.GET(value));
				} else {
					_validators.add(MaxStringValidator.GET(value));
				}
			}
			
			if (parameter.isAnnotationPresent(Regex.class)) {
				_validators.add(RegexValidator.GET(parameter.getAnnotation(Regex.class).value()));
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
			
			Min min = parameter.getAnnotation(Min.class);
			if (min != null) {
				if (min.strict()) {
					_validators.add(MinStrictValidator.GET(min.value()));
				} else {
					_validators.add(MinValidator.GET(min.value()));
				}
			}
			
			Max max = parameter.getAnnotation(Max.class);
			if (max != null) {
				if (max.strict()) {
					_validators.add(MaxStrictValidator.GET(max.value()));
				} else {
					_validators.add(MaxValidator.GET(max.value()));
				}
			}
			
		} else if (Model.class.isAssignableFrom(type)) {
			
			// No validator, currently, is available to the models
			
		} else if (LocalDate.class.equals(type)) {

			Before before = parameter.getAnnotation(Before.class);
			if (before != null) {
				LocalDate value;
				if ((value = Parser.parseDate(before.value())) == null) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, before, "Must be a valid Date");
				}
				
				_validators.add(BeforeValidator.GET(value));
			}
			
			After after = parameter.getAnnotation(After.class);
			if (after != null) {
				LocalDate value;
				if ((value = Parser.parseDate(after.value())) == null) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, after, "Must be a valid Date");
				}
				
				_validators.add(AfterValidator.GET(value));
			}

			Younger younger = parameter.getAnnotation(Younger.class);
			if (younger != null) {
				PeriodHolder value;
				if ((value = PeriodHolder.parseOrNull(younger.value())) == null) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, younger, "Must be a valid PeriodHolder representation");
				}
				
				_validators.add(YoungerValidator.GET(value));
			}

			Older older = parameter.getAnnotation(Older.class);
			if (older != null) {
				PeriodHolder value;
				if ((value = PeriodHolder.parse(older.value())) == null) {
					throw new InvalidMappingMethodParamValidator(webController, method, parameter, older, "Must be a valid PeriodHolder representation");
				}
				
				_validators.add(OlderValidator.GET(value));
			}
			
		} else if (Boolean.class.equals(type)) {
			
			// No validator, currently, available for Boolean
			
		} else if (Character.class.equals(type)) {
			
			// No validator, currently, available for Character
			
		} else {
			throw new UnsupportedType(type);
		}
		
		this.validators = _validators.toArray(new Validator [0]);
		
		
		List <Consumer> _consumers = new ArrayList <> ();
		
		if (String.class.equals(type)) {
			
			if (parameter.isAnnotationPresent(Default.class)) {
				_consumers.add(DefaultConsumer.GET(parameter.getAnnotation(Default.class).value()));
			}
			
			if (parameter.isAnnotationPresent(Capitalize.class)) {
				if (parameter.getAnnotation(Capitalize.class).value()) {
					_consumers.add(ForcedCapitalizeConsumer.GET());
				} else {
					_consumers.add(CapitalizeConsumer.GET());
				}
			}
			
			if (parameter.isAnnotationPresent(UpperCase.class)) {
				_consumers.add(UpperCaseConsumer.GET());
			}
			
			if (parameter.isAnnotationPresent(LowerCase.class)) {
				_consumers.add(LowerCaseConsumer.GET());
			}
			
		} else if (Number.class.isAssignableFrom(type)) {
			
			Default _default = parameter.getAnnotation(Default.class);
			if (_default != null) {
				Double value = Parser.parseDouble(_default.value());
				if (value == null) {
					throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a number");
				}
				
				_consumers.add(DefaultNumberConsumer.GET((Class<? extends Number>) type, value));
			}
			
		} else if (Model.class.isAssignableFrom(type)) {
			
			Default _default = parameter.getAnnotation(Default.class);
			if (_default != null) {
				Long value = Parser.parseLong(_default.value());
				if (value == null) {
					throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be same type as the models id"); // TODO
				}
				
				_consumers.add(DefaultModelConsumer.GET((Class<? extends Model<?>>) type, value));
			}
			
		} else if (LocalDate.class.equals(type)) {
			
			Default _default = parameter.getAnnotation(Default.class);
			if (_default != null) {
				LocalDate value = Parser.parseDate(_default.value());
				if (value == null) {
					if (_default.value().equalsIgnoreCase("now")) {
						_consumers.add(DefaultNowDateConsumer.GET());
					} else {
						throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a valid date or `now`");
					}
				} else {
					_consumers.add(DefaultDateConsumer.GET(value));
				}
			}
			
		} else if (Boolean.class.equals(type)) {
			
			// TODO add false / true and thus remove flag
			if (parameter.isAnnotationPresent(Flag.class)) {
				_consumers.add(FlagConsumer.GET());
			}
			
		} else if (Character.class.equals(type)) {
			
			Default _default = parameter.getAnnotation(Default.class);
			if (_default != null) {
				Character value = Parser.parseChar(_default.value());
				if (value == null) {
					throw new InvalidMappingMethodParamConsumer(webController, method, parameter, _default, "Must be a single character");
				}
				_consumers.add(DefaultCharConsumer.GET(value));
			}
			
			if (parameter.isAnnotationPresent(UpperCase.class)) {
				_consumers.add(UpperCaseCharConsumer.GET());
			}
			
			if (parameter.isAnnotationPresent(LowerCase.class)) {
				_consumers.add(LowerCaseCharConsumer.GET());
			}
			
		} else {
			throw new UnsupportedType(type);
		}

		this.consumers = _consumers.toArray(new Consumer [0]);
	}
	
	public Object getParam (HttpServletRequest request) throws InvalidParameter {
		Object value;
		
		if (primitive) {
			value = getParameter(name, type, request);
		} else {
			value = getEntity(name, type, request);
		}
		
		validate(value);
		
		return consume(value);
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

	// TODO add arrays or lists, also check if u can instantiate an array of type clase <?>
	private static Object getEntity (String name, Class <?> type, HttpServletRequest request) {
		// FIXME AYAYYAYAYAYAYAYAYYAYAYAYAYYA 
		Long id = (Long) getParameter(name, Long.class, request); // TODO ayay, something with objects instead. does this aslan work, cast null, yes casting null works
		if (id == null) {
			return null;
		} else {
			return Manager.find(type, id);
		}
	}
	
	private static Object getParameter (String name, Class <?> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		
		if (parameter == null || String.class.equals(type)) {
			return parameter;
			
		} else if (Boolean.class.equals(type)) {
			return Parser.parseBool(parameter);
			
		} else if (Integer.class.equals(type)) {
			return Parser.parseInt(parameter);
			
		} else if (Float.class.equals(type)) {
			return Parser.parseFloat(parameter);
			
		} else if (Double.class.equals(type)) {
			return Parser.parseDouble(parameter);
			
		} else if (Long.class.equals(type)) {
			return Parser.parseLong(parameter);
			
		} else if (LocalDate.class.equals(type)) {
			return Parser.parseDate(parameter);
			
		} else if (Character.class.equals(type)) {
			return Parser.parseChar(parameter);
			
		} else if (Short.class.equals(type)) {
			return Parser.parseShort(parameter);
			
		} else if (Byte.class.equals(type)) {
			return Parser.parseByte(parameter);
			
		} else {
			throw new UnsupportedType(type);
		}
	}
	
}
