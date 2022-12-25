package controllers.controller.core;

import controllers.controller.core.annotations.Name;
import controllers.controller.core.consumers.*;
import controllers.controller.core.consumers.annotations.*;
import controllers.controller.core.validators.*;
import controllers.controller.core.validators.annotations.*;
import controllers.controller.exceptions.InvalidMappingMethodParam;
import controllers.controller.exceptions.InvalidMappingMethodParamConsumer;
import controllers.controller.exceptions.InvalidMappingMethodParamValidator;
import controllers.controller.exceptions.InvalidParam;
import dao.Manager;
import jakarta.servlet.http.HttpServletRequest;
import models.core.Model;
import util.Dates.DateValuesHolder;
import util.exceptions.UnsupportedType;
import util.Parser;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Param {

	private Class <?> type;
	/**
	 * Primitive here means anything other than a Model.
	 * Primitives themselves (int, float..) are not allowed
	 */
	private boolean primitive;
	private String name;
	private Validator [] validators;
	private Consumer [] consumers;
	
	public Param(Class <?> controller, Method method, Parameter parameter) {
		this.type = parameter.getType();
		
		if (type.isPrimitive()) {
			throw new InvalidMappingMethodParam(controller, method, parameter, "Primitives aren't allowed");
		}
		
		this.primitive = !Model.class.isAssignableFrom(type);
		
		this.name = (parameter.isAnnotationPresent(Name.class))? parameter.getAnnotation(Name.class).value() : parameter.getName();
		
		
		List <Validator> _validators = new ArrayList <> ();
		
		if (!parameter.isAnnotationPresent(Required.class) || parameter.getAnnotation(Required.class).value()) {
			_validators.add(RequiredValidator.GET());
		}
		
		if (String.class.equals(type)) {

			if (parameter.getAnnotation(NonBlank.class) != null) {
				_validators.add(NonBlankValidator.GET());
			}

			Min min;
			if ((min = parameter.getAnnotation(Min.class)) != null) {
				int value = (int) min.value();
				if (value != min.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(controller, method, parameter, min.toString(), "Must be a positive or null integer value");
				}
				
				if (min.strict()) {
					_validators.add(MinStrictStringValidator.GET(value));
				} else {
					_validators.add(MinStringValidator.GET(value));
				}
			}
			
			Max max;
			if ((max = parameter.getAnnotation(Max.class)) != null) {
				int value = (int) max.value();
				if (value != max.value() || value < 0) {
					throw new InvalidMappingMethodParamValidator(controller, method, parameter, max.toString(), "Must be a positive or null integer value");
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
				if (parameter.getAnnotation(Alphabetic.class).value()) {
					_validators.add(RegexValidator.GET("^[a-zA-Z_]+$"));
				} else {
					_validators.add(RegexValidator.GET("^[a-zA-Z]+$"));
				}
			}
			
			if (parameter.isAnnotationPresent(Alphanumeric.class)) {
				if (parameter.getAnnotation(Alphanumeric.class).value()) {
					_validators.add(RegexValidator.GET("^[a-zA-Z0-9_]+$"));
				} else {
					_validators.add(RegexValidator.GET("^[a-zA-Z0-9]+$"));
				}
			}
			
		} else if (Number.class.isAssignableFrom(type)) {
			
			Min min;
			if ((min = parameter.getAnnotation(Min.class)) != null) {
				if (min.strict()) {
					_validators.add(MinStrictValidator.GET(min.value()));
				} else {
					_validators.add(MinValidator.GET(min.value()));
				}
			}
			
			Max max;
			if ((max = parameter.getAnnotation(Max.class)) != null) {
				if (max.strict()) {
					_validators.add(MaxStrictValidator.GET(max.value()));
				} else {
					_validators.add(MaxValidator.GET(max.value()));
				}
			}
			
		} else if (Model.class.isAssignableFrom(type)) {
			
			
		} else if (LocalDate.class.equals(type)) {

			Before before;
			if ((before = parameter.getAnnotation(Before.class)) != null) {
				LocalDate value;
				if ((value = Parser.parseDate(before.value())) == null) {
					throw new InvalidMappingMethodParamValidator(controller, method, parameter, before.toString(), "Must be a valid Date");
				}
				
				_validators.add(BeforeValidator.GET(value));
			}
			
			After after;
			if ((after = parameter.getAnnotation(After.class)) != null) {
				LocalDate value;
				if ((value = Parser.parseDate(after.value())) == null) {
					throw new InvalidMappingMethodParamValidator(controller, method, parameter, after.toString(), "Must be a valid Date");
				}
				
				_validators.add(AfterValidator.GET(value));
			}

			Younger younger;
			if ((younger = parameter.getAnnotation(Younger.class)) != null) {
				DateValuesHolder value;
				if ((value = DateValuesHolder.parse(younger.value())) == null) {
					throw new InvalidMappingMethodParamValidator(controller, method, parameter, younger.toString(), "Must be a valid Date Values Holder");
				}
				
				_validators.add(YoungerValidator.GET(value));
			}

			Older older;
			if ((older = parameter.getAnnotation(Older.class)) != null) {
				DateValuesHolder value;
				if ((value = DateValuesHolder.parse(older.value())) == null) {
					throw new InvalidMappingMethodParamValidator(controller, method, parameter, older.toString(), "Must be a valid Date Values Holder");
				}
				
				_validators.add(OlderValidator.GET(value));
			}
			
		} else if (Boolean.class.equals(type)) {
			
			
		} else if (Character.class.equals(type)) {
			
			
		} else {
			throw new InvalidMappingMethodParam(controller, method, parameter, "Unsuported type: " +type);
		}
		
		this.validators = new Validator [_validators.size()];
		for (int i = 0; i < validators.length; i++) {
			this.validators[i] = _validators.get(i);
		}
		
		
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
			
			Default _default;
			if ((_default = parameter.getAnnotation(Default.class)) != null) {
				Double value;
				if ((value = Parser.parseDouble(_default.value())) == null) {
					throw new InvalidMappingMethodParamConsumer(controller, method, parameter, _default.toString(), "Must be a number");
				}
				
				_consumers.add(DefaultNumberConsumer.GET(type, value));
			}
			
		} else if (Model.class.isAssignableFrom(type)) {
			
			Default _default;
			if ((_default = parameter.getAnnotation(Default.class)) != null) {
				Long value;
				if ((value = Parser.parseLong(_default.value())) == null) {
					throw new InvalidMappingMethodParamConsumer(controller, method, parameter, _default.toString(), "Must be same type as the models id");
				}
				
				_consumers.add(DefaultModelConsumer.GET(type, value));
			}
			
		} else if (LocalDate.class.equals(type)) {
			
			Default _default;
			if ((_default = parameter.getAnnotation(Default.class)) != null) {
				LocalDate value;
				if ((value = Parser.parseDate(_default.value())) == null) {
					if (_default.value().equalsIgnoreCase("now")) {
						_consumers.add(DefaultNowDateConsumer.GET());
					} else {
						throw new InvalidMappingMethodParamConsumer(controller, method, parameter, _default.toString(), "Must be a valid date or 'now'");
					}
				} else {
					_consumers.add(DefaultDateConsumer.GET(value));
				}
			}
			
		} else if (Boolean.class.equals(type)) {
			
			if (parameter.isAnnotationPresent(Flag.class)) {
				_consumers.add(FlagConsumer.GET());
			}
			
		} else if (Character.class.equals(type)) {
			
			Default _default;
			if ((_default = parameter.getAnnotation(Default.class)) != null) {
				Character value;
				if ((value = Parser.parseChar(_default.value())) == null) {
					throw new InvalidMappingMethodParamConsumer(controller, method, parameter, _default.toString(), "Must be a char");
				}
				_consumers.add(DefaultCharConsumer.GET(value));
			}
			
			if (parameter.isAnnotationPresent(UpperCase.class)) {
				_consumers.add(UpperCaseCharConsumer.GET());
			}
			
			if (parameter.isAnnotationPresent(LowerCase.class)) {
				_consumers.add(LowerCaseCharConsumer.GET());
			}
			
		}

		this.consumers = new Consumer [_consumers.size()];
		for (int i = 0; i < consumers.length; i++) {
			this.consumers[i] = _consumers.get(i);
		}
		
	}
	
	public Object getParam (HttpServletRequest request) {
		Object value;
		
		if (primitive) {
			value = getParam(name, type, request);
		} else {
			value = getEntity(name, type, request);
		}
		
		validate(value);
		
		return consume(value);
	}
	
	private void validate (Object value) {
		for (Validator validator : validators) {
			if (validator.notValidate(value)) {
				throw new InvalidParam(this, validator);
			}
		}
	}
	
	private Object consume (Object value) {
		for (Consumer consumer : consumers) {
			value = consumer.consume(value);
		}
		
		return value;
	}

	public static Object getEntity (String name, Class <?> type, HttpServletRequest request) {
		Long id = (Long) getParam(name, Long.class, request);
		if (id == null) {
			return null;
		} else {
			return Manager.find(type, id);
		}
	}
	
	private static Object getParam (String name, Class <?> type, HttpServletRequest request) {
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
