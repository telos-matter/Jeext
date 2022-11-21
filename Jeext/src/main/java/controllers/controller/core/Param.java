package controllers.controller.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import controllers.controller.Controller;
import controllers.controller.core.annotations.GetMapping;
import controllers.controller.core.annotations.Path;
import controllers.controller.core.annotations.PostMapping;
import controllers.controller.core.annotations.Query;
import controllers.controller.core.annotations.Request;
import controllers.controller.core.validators.*;
import controllers.controller.core.validators.annotations.*;
import controllers.controller.exceptions.InvalidParam;
import controllers.controller.exceptions.UnsupportedType;
import jakarta.servlet.http.HttpServletRequest;
import models.Model;
import util.Strings;

// NOTICE: Other annotations taht dont apply are ignored
// NOTICE: only class types not primitives
// NOTICE: two param source -> first

public class Param {

	private Class <?> type;
	private String name;
	private Validator [] validators;
	private Source source;

	public Param(Method method, Parameter parameter) {
		this.type = parameter.getType();
		this.name = parameter.getName();
		
		List <Validator> _validators = new ArrayList <> ();
		
		if (!parameter.isAnnotationPresent(Required.class) || parameter.getAnnotation(Required.class).value()) {
			_validators.add(RequiredValidator.GET());
		}
		
		if (String.class.equals(type)) {
			
			// TODO: Regex blank length..
			
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
			
			
		} else {
			throw new UnsupportedType(type, true);
		}
		
		this.validators = new Validator [_validators.size()];
		for (int i = 0; i < validators.length; i++) {
			validators[i] = _validators.get(i);
		}
		
		if (parameter.isAnnotationPresent(Request.class)) {
			this.source = Source.REQUEST;
		} else if (parameter.isAnnotationPresent(Path.class)) {
			this.source = Source.PATH;
		} else if (parameter.isAnnotationPresent(Query.class)) {
			this.source = Source.QUERY;
		} else if (method.isAnnotationPresent(GetMapping.class)) {
			this.source = Source.QUERY;
		} else if (method.isAnnotationPresent(PostMapping.class)) {
			this.source = Source.REQUEST;
		}
		
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

	private static Object getParameter (String name, Class <?> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		if (parameter == null) {
			return null;
		} else if (String.class.equals(type)) {
			return parameter;
		} else if (Long.class.equals(type)) {
			return Strings.parseLong(parameter);
		} else if (Boolean.class.equals(type)) {
			return Strings.parseBool(parameter);
		} else if (Double.class.equals(type)) {
			return Strings.parseDouble(parameter);
		} else if (Date.class.equals(type)) {
			return Strings.parseDate(parameter);
		} else {
			throw new UnsupportedType(type, false);
		}
	}
	
	public enum Source {
		REQUEST,
		PATH,
		QUERY;
	}
	
}

/**
 * 
 * //	public static <T> List <T> assertEntities (String id_parameter, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		List <Integer> ids = assertParameters (id_parameter, Integer.class, request, response);
//		if (ids == null) {
//			return null;
//		} else {
//			List <T> entities = new ArrayList <> ();
//			for (Integer id : ids) {
//				T entity = Manager.find(type, id);
//				if (entity == null) {
//					setError(404, "The requested " +type.getSimpleName() +" doesn't exist.", request, response);
//					return null;
//				}
//				entities.add(entity);
//			}
//			return entities;
//		}
//	}
//	
//	public static <T> List <T> assertParameters (String name, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		String [] request_parameters = request.getParameterValues(name);
//		if (request_parameters == null) {
//			setError(400, "The HTTP request is incomplete.", request, response);
//			return null;
//		} else if (Integer.class.equals(type)) {
//			List <T> parameters = new ArrayList <T> ();
//			Integer parameter = null;
//			for (String request_parameter : request_parameters) {
//				parameter = StringManager.parseInt(request_parameter);
//				if (parameter == null) {
//					setError(400, "The HTTP request is corrupted.", request, response);
//					return null;
//				}
//				parameters.add((T) parameter);
//			}
//			return parameters;
//		} else if (Boolean.class.equals(type)) {
//			List <T> parameters = new ArrayList <T> ();
//			Boolean parameter = null;
//			for (String request_parameter : request_parameters) {
//				parameter = StringManager.parseBool(request_parameter);
//				if (parameter == null) {
//					setError(400, "The HTTP request is corrupted.", request, response);
//					return null;
//				}
//				parameters.add((T) parameter);
//			}
//			return parameters;
//		} else if (Float.class.equals(type)) {
//			List <T> parameters = new ArrayList <T> ();
//			Float parameter = null;
//			for (String request_parameter : request_parameters) {
//				parameter = StringManager.parseFloat(request_parameter);
//				if (parameter == null) {
//					setError(400, "The HTTP request is corrupted.", request, response);
//					return null;
//				}
//				parameters.add((T) parameter);
//			}
//			return parameters;
//		} else if (Date.class.equals(type)) {
//			List <T> parameters = new ArrayList <T> ();
//			Date parameter = null;
//			for (String request_parameter : request_parameters) {
//				parameter = StringManager.parseDate(request_parameter);
//				if (parameter == null) {
//					setError(400, "The HTTP request is corrupted.", request, response);
//					return null;
//				}
//				parameters.add((T) parameter);
//			}
//			return parameters;
//		} else {
//			System.out.println("An unsupported type (" +type +") parameter in assertParameters.");
//			setError(500, "An unsupported type (" +type +") parameter in assertParameters.", request, response);
//			return null;
//		}
//	}
//	
//	public static <T> T assertEntity (Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		return assertEntity("id", type, request, response);
//	}
//	
//	public static <T> T assertEntity (String id_parameter, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		Integer id = assertParameter (id_parameter, Integer.class, request, response);
//		if (id == null) {
//			return null;
//		} else {
//			T entity = Manager.find(type, id);
//			if (entity == null) {
//				setError(404, "The requested " +type.getSimpleName() +" doesn't exist.", request, response);
//			}
//			return entity;
//		}
//	}
//	
//	public static <T> T assertParameter (String name, Class <T> type, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		String request_parameter = request.getParameter(name);
//		if (request_parameter == null) {
//			setError(400, "The HTTP request is incomplete.", request, response);
//			return null;
//		} else if (Integer.class.equals(type)) {
//			Integer parameter = StringManager.parseInt(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			} else {
//				return (T) parameter;
//			}
//		} else if (Boolean.class.equals(type)) {
//			Boolean parameter = StringManager.parseBool(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			} else {
//				return (T) parameter;
//			}
//		} else if (Float.class.equals(type)) {
//			Float parameter = StringManager.parseFloat(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			} else {
//				return (T) parameter;
//			}
//		} else if (Date.class.equals(type)) {
//			Date parameter = StringManager.parseDate(request_parameter);
//			if (parameter == null) {
//				setError(400, "The HTTP request is corrupted.", request, response);
//				return null;
//			} else {
//				return (T) parameter;
//			}
//		} else {
//			System.out.println("An unsupported type (" +type +") parameter in assertParameter.");
//			setError(500, "An unsupported type (" +type +") parameter in assertParameter.", request, response);
//			return null;
//		}
//	}
//	
//	public static <T> T getEntity (Class <T> type, HttpServletRequest request) {
//		return getEntity("id", type, request);
//	}
//	
//	public static <T> T getEntity (String id_parameter, Class <T> type, HttpServletRequest request) {
//		Integer id = getParameter (id_parameter, Integer.class, request);
//		if (id == null) {
//			return null;
//		} else {
//			return Manager.find(type, id);
//		}
//	}
//	
	
//	public static <T> T getParameter (String name, Class <T> type, HttpServletRequest request) {
//		String parameter = request.getParameter(name);
//		if (parameter == null) {
//			return null;
//		} else if (Integer.class.equals(type)) {
//			return (T) StringManager.parseInt(parameter);
//		} else if (Boolean.class.equals(type)) {
//			return (T) StringManager.parseBool(parameter);
//		} else if (Float.class.equals(type)) {
//			return (T) StringManager.parseFloat(parameter);
//		} else if (Date.class.equals(type)) {
//			return (T) StringManager.parseDate(parameter);
//		} else {
//			System.out.println("An unsupported type (" +type +") parameter in getParameter.");
//			return null;
//		}
//	}
	
	public static <T> T getParameter (String name, Class <T> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		if (parameter == null) {
			return null;
		} else if (String.class.equals(type)) {
			return (T) parameter;
		} else if (Long.class.equals(type)) {
			return (T) Strings.parseLong(parameter);
		} else if (Boolean.class.equals(type)) {
			return (T) Strings.parseBool(parameter);
		} else if (Double.class.equals(type)) {
			return (T) Strings.parseDouble(parameter);
		} else if (Date.class.equals(type)) {
			return (T) Strings.parseDate(parameter);
		} else {
			throw new UnsupportedType(type);
		}
	}
	
//	
//	public static <T> void setCreatedSucces (String name, Class <T> clazz, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		if (clazz == null) {
//			setSucces(201,  "" +name +" has been created succesfully.", request, response);
//		} else {
//			setSucces(201,  "The " +name +" " + clazz.getSimpleName()+" has been created succesfully.", request, response);
//		}
//	}
//	
//	public static <T> void setDeletedFailure (String name, Class <T> clazz, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		if (clazz == null) {
//			setError(409,  "" +name +" was not deleted as that other parts currently use it.", request, response);
//		} else {
//			setError(409,  "The " +name +" " + clazz.getSimpleName()+" was not deleted as that other parts currently use it.", request, response);
//		}
//	}
//	
//	public static <T> void setDeletedSucces (String name, Class <T> clazz, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		if (clazz == null) {
//			setSucces(200,  "" +name +" has been deleted succesfully.", request, response);
//		} else {
//			setSucces(200,  "The " +name +" " + clazz.getSimpleName()+" has been deleted succesfully.", request, response);
//		}
//	}
//	
//	public static <T> void setUpdatedSucces (String name, Class <T> clazz, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		if (clazz == null) {
//			setSucces(200,  "" +name +" has been updated succesfully.", request, response);
//		} else {
//			setSucces(200,  "The " +name +" " + clazz.getSimpleName()+" has been updated succesfully.", request, response);
//		}
//	}
//	
//	public static void setInvalideFormError (HttpServletRequest request, HttpServletResponse response) throws IOException {
//		setError(400, "Invalide form data.", request, response);
//	}
//	
//	public static void setCorruptedData (HttpServletRequest request, HttpServletResponse response) throws IOException {
//		setError(400, "Corrupted data.", request, response);
//	}
//	
//	public static void add404Message (HttpServletRequest request) {
//		request.setAttribute("message", "This URL: " +request.getRequestURI().replaceFirst(request.getContextPath(), "") +"\nDoesn't seem to be a one we use.");
//	}
//	
//	public static void addErrorMessage (String message, HttpServletRequest request) {
//		request.setAttribute("message", message);
//	}
//	
//	public static void setError (int code, String message, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setAttribute("error_code", code);
//		request.setAttribute("message", message);
//		response.sendError(code);
//	}
//	
//	public static void setSucces (int code, String message, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setAttribute("succes_code", code);
//		request.setAttribute("message", message);
//		response.sendError(code);
//	}
 * 
 */
