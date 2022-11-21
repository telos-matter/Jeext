package controllers.controller.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.controller.core.annotations.Name;
import controllers.controller.core.consumers.Consumer;
import controllers.controller.core.consumers.FlagConsumer;
import controllers.controller.core.consumers.annotations.Flag;
import controllers.controller.core.validators.*;
import controllers.controller.core.validators.annotations.*;
import controllers.controller.exceptions.InvalidMappingMethodParam;
import controllers.controller.exceptions.InvalidParam;
import controllers.controller.exceptions.UnsupportedType;
import dao.Manager;
import jakarta.servlet.http.HttpServletRequest;
import models.Model;
import util.Parser;

// NOTICE: Other annotations that don't apply are ignored
// NOTICE: only class types no primitives
// NOTICE: consumers should be commutative

public class Param {

	private Class <?> type;
	private boolean primitive; // NOTICE Anything other than a Model is considered primitive
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
			
			// TODO: Regex blank length min max..
			
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
			
			
		} else if (Date.class.equals(type)) {
			
			
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
			
		} else if (Number.class.isAssignableFrom(type)) {
			
		} else if (Model.class.isAssignableFrom(type)) {
			
			
		} else if (Date.class.equals(type)) {
			
			
		} else if (Boolean.class.equals(type)) {
			
			if (parameter.isAnnotationPresent(Flag.class)) {
				_consumers.add(FlagConsumer.GET());
			}
			
		} else if (Character.class.equals(type)) {
			
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
			
		} else if (Date.class.equals(type)) {
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
