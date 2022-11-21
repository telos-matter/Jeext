package controllers.controller.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;

import controllers.controller.Controller;
import controllers.controller.annotations.GetMapping;
import controllers.controller.annotations.PostMapping;
import controllers.controller.core.validators.Required;
import controllers.controller.core.validators.Validator;
import controllers.controller.exceptions.InvalidParam;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.http.HttpServletRequest;
import util.StringManager;

public class Param {

	private Class <?> type;
	private String name;
	private Validator [] validators;
	private Source source;

	public Param(Method method, Parameter parameter) {
		this.type = parameter.getType();
		this.name = parameter.getName();
		
		this.validators = new Validator [1];
		validators [0] = Required.REQUIRED;
		
//		if (method.isAnnotationPresent(GetMapping.class)) {
//			
//		}
		
		// TODO: implement ^^^^
		
		this.source = Source.QUERY;
	}
	
	public Object getParam (HttpServletRequest request) {
		Object value = null;
		
		switch (source) {
		case REQUEST: break;
		case PATH: break;
		case QUERY:
			value = getParameter(name, type, request);
			break;
		}
		
		validate(value);
		
		return value;
	}
	
	private void validate (Object value) {
		for (Validator validator : validators) {
			if (validator.notValidate(value)) {
				throw new InvalidParam(name, validator);
			}
		}
	}

	private static <T> T getParameter (String name, Class <T> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		if (parameter == null) {
			return null;
		} else if (String.class.equals(type)) {
			return (T) parameter;
		} else if (Long.class.equals(type)) {
			return (T) StringManager.parseLong(parameter);
		} else if (Boolean.class.equals(type)) {
			return (T) StringManager.parseBool(parameter);
		} else if (Double.class.equals(type)) {
			return (T) StringManager.parseDouble(parameter);
		} else if (Date.class.equals(type)) {
			return (T) StringManager.parseDate(parameter);
		} else {
			throw new UnsupportedType(type);
		}
	}
	
	public enum Source {
		REQUEST,
		PATH,
		QUERY;
	}
	
}
