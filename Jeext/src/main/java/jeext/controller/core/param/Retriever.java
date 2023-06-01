package jeext.controller.core.param;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jeext.controller.core.param.Param.FailedParamInit;
import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.annotations.composer.ComposeWith;
import jeext.dao.Manager;
import jeext.model.Model;
import jeext.util.Parser;
import jeext.util.exceptions.FailedAssertion;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledDevException;
import jeext.util.exceptions.UnsupportedType;

// Basically param, but withouth validators or consumers
// Only retrieves a parameter form request
public class Retriever {
	
	protected String name;

	protected Class <?> type;
	protected Multiplicity multiplicity; 
	/**
	 * Primitive here means anything other than a Model.
	 * Primitives themselves (int, float..) are not allowed
	 */
	protected Kind kind;
	
	protected Class <?> idType;
	protected IDKind idKind;
	
//	TODO Commented out until commit
//	protected Retriever(String name, Class <?> type, Multiplicity multiplicity, Kind kind, Class <?> idType, IDKind idKind) {
//		PassedNull.check(name, String.class);
//		PassedNull.check(type, Class.class);
//		PassedNull.check(multiplicity, Multiplicity.class);
//		PassedNull.check(kind, Kind.class);
//
//		if (kind != Kind.MODEL && (idType != null || idKind != null)) {
//			throw new FailedAssertion(String.format("Illegal arguments combination; kind: `%s`, idType: `%s`, idKind: `%s`", kind, idType, idKind));
//		}
//		
//		if (kind == Kind.MODEL && (idType == null || idKind == null)) {
//			throw new FailedAssertion(String.format("Illegal arguments combination; kind: `%s`, idType: `%s`, idKind: `%s`", kind, idType, idKind));
//		}
//		
//		if (!isTypeSupported(type)) {
//			throw new UnsupportedType(type);
//		}
//		
//		if (kind == Kind.MODEL && !isIDTypeSupported(idType)) {
//			throw new UnsupportedType(idType);
//		}
//		
//		this.name = name;
//		
//		this.type = type;
//		this.multiplicity = multiplicity;
//		
//		this.kind = kind;
//		
//		this.idType = idType;
//		this.idKind = idKind;
//	}
	
	protected Retriever (Parameter parameter) throws FailedParamInit {
		name = (parameter.isAnnotationPresent(Name.class))? parameter.getAnnotation(Name.class).value() : parameter.getName();
		
		
		type = parameter.getType();
		
		if (type.isArray()) {
			type = type.componentType();
			multiplicity = Multiplicity.ARRAY;
			
		} else if (List.class.isAssignableFrom(type)) {
			Type genericType = parameter.getParameterizedType();
			if (genericType instanceof ParameterizedType parameterizedType) {
				type = (Class <?>) parameterizedType.getActualTypeArguments()[0];
				multiplicity = Multiplicity.LIST;
				
			} else {
				throw new UnsupportedType(genericType); // IDK
			}
			
		} else {
			multiplicity = Multiplicity.SINGLE;
		}
	
		
		processType(parameter);
	}

	protected Retriever (Field field) throws FailedParamInit {
		if (field.isAnnotationPresent(ComposeWith.class)) {
			ComposeWith composeWith = field.getAnnotation(ComposeWith.class);
			name = (composeWith.value().isBlank())? field.getName() : composeWith.value();
		} else {
			name = field.getName();
		}

		
		type = field.getType();
		
		if (type.isArray()) {
			type = type.componentType();
			multiplicity = Multiplicity.ARRAY;
			
		} else if (List.class.isAssignableFrom(type)) {
			Type genericType = field.getGenericType();
			if (genericType instanceof ParameterizedType parameterizedType) {
				type = (Class <?>) parameterizedType.getActualTypeArguments()[0];
				multiplicity = Multiplicity.LIST;
				
			} else {
				throw new UnsupportedType(genericType); // IDK
			}
			
		} else {
			multiplicity = Multiplicity.SINGLE;
		}
		
		
		processType(field);
	}
	
	private void processType (Object owner) throws FailedParamInit {
		validateType(owner);
		
		determineKind();
		
		if (kind == Kind.MODEL) {
			idType = validateModelAndGetIdType(owner);
			
			validateIdType(owner);
			
			determineIdKind(owner);
		}
	}

	private void validateType (Object owner) throws FailedParamInit {
		if (type.isPrimitive()) {
			throw new FailedParamInit("(" +owner  +") Primitives aren't allowed, use their Object representation instead.");
		}
		
		if (!Retriever.isTypeSupported(type)) {
			throw new FailedParamInit("(" +owner  +") Unsuported type `" +type +"`");
		}
	}
	
	private void determineKind () {
		if (Model.class.isAssignableFrom(type)) {
			kind = Kind.MODEL;
		} else if (Enum.class.isAssignableFrom(type)) {
			kind = Kind.ENUM;
		} else {
			kind = Kind.PRIMITIVE;
		}
	}

	private Class <?> validateModelAndGetIdType (Object owner) throws FailedParamInit {
		if (kind != Kind.MODEL) {
			throw new FailedAssertion("Kind is not model for (" +owner  +") , instead: " +kind);
		}
		
		List <Field> id = Arrays
				.stream(type.getDeclaredFields())
				.filter((Field field) -> {return field.isAnnotationPresent(MID.class);})
				.toList();
		
		if (id.size() != 1) {
			throw new FailedParamInit("(" +owner  +") The Model has to indentify one single ID field with the `" +MID.class +"` annotation");
		}
		
		return id.get(0).getType();
	}

	
	private void validateIdType(Object owner) throws FailedParamInit {
		if (idType.isPrimitive()) {
			throw new FailedParamInit("(" +owner  +") Primitives can't be used as a models' id, use their Object representation instead");
		}
		
		if (!Retriever.isIDTypeSupported(idType)) {
			throw new FailedParamInit("(" +owner  +") Unsuported model id type `" +idType +"`");
		}
	}

	private void determineIdKind(Object owner) {
		if (kind != Kind.MODEL) {
			throw new FailedAssertion("Kind is not modelfor (" +owner  +") , instead: " +kind);
		}
		
		if (Enum.class.isAssignableFrom(idType)) {
			idKind = IDKind.ENUM;
		} else {
			idKind = IDKind.PRIMITIVE;
		}
	}
	
	protected static boolean isTypeSupported (Class <?> type) {
		PassedNull.check(type, Class.class);
		
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
	
	protected static boolean isIDTypeSupported (Class <?> idType) {
		PassedNull.check(idType, Class.class);
		
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
			return false;
		} else {
			return true;
		}
	}
	
	protected Object retrieve (HttpServletRequest request) {
		try {
			
			switch (multiplicity) {
			case SINGLE:
				
				switch (kind) {
				case PRIMITIVE:
					return getParameter(name, type, request);
					
				case ENUM:
					return getEnum(name, type, request);
					
				case MODEL:
					return getEntity(name, type, idType, idKind, request);
					
				default:
					throw new UnsupportedType(kind);
				}
				
			case ARRAY:
				
				switch (kind) {
				case PRIMITIVE:
					return getParameters(name, type, request);
					
				case ENUM:
					return getEnums(name, type, request);
					
				case MODEL:
					return getEntities(name, type, idType, idKind, request);
					
				default:
					throw new UnsupportedType(kind);
				}

			case LIST:
				
				Object array = switch (kind) {
				case PRIMITIVE: 
					yield getParameters(name, type, request);
					
				case ENUM:
					yield getEnums(name, type, request);
					
				case MODEL:
					yield getEntities(name, type, idType, idKind, request);
					
				default:
					throw new UnsupportedType(kind);
				};
				
				return constructList(array, type);
				
			default:
				throw new UnsupportedType(multiplicity);
			}
			
		} catch (Exception e) { // In case I missed something with all the casting going on
			throw new UnhandledDevException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List <T> constructList (Object array, Class <T> type) {
		if (array == null) {
			return null;
		}
		
		return Arrays.asList((T []) array);
	}
	
	private static <T, S> T getEntity (String name, Class <T> type, Class <S> idType, IDKind idKind, HttpServletRequest request) {
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
	private static <T, S> T [] getEntities (String name, Class <T> type, Class <S> idType, IDKind idKind, HttpServletRequest request) {
		S [] ids;
		switch (idKind) {
		case PRIMITIVE:
			ids = getParameters(name, idType, request);
			break;
		case ENUM:
			ids = getEnums(name, idType, request);
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
		return parseEnum(constant, type);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T [] getEnums (String name, Class <T> type, HttpServletRequest request) {
		String [] constants = getParameters(name, String.class, request);
		if (constants == null) {
			return null;
		}
		
		T [] enums = (T []) Array.newInstance(type, constants.length);
		for (int i = 0; i < enums.length; i++) {
			enums[i] = parseEnum(constants[i], type);
		}
		return enums;
	}

	private static <T> T getParameter (String name, Class <T> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		return parse(parameter, type);
	}

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
	protected static <T, S extends Enum <S>> T parseEnum (String constant, Class <T> type) {
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
	
	protected static <T> T parse (String s, Class <T> type) {
		try {
			return Parser.parse(s, type);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new UnsupportedType(type);
		}
	}
	
	protected static enum Multiplicity {
		SINGLE,
		ARRAY,
		LIST;
	}
	
	protected static enum Kind {
		PRIMITIVE,
		ENUM,
		MODEL;
	}
	
	protected static enum IDKind {
		PRIMITIVE,
		ENUM;
	}
	
}
