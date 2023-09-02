package jeext.controller.core.param;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jeext.controller.core.param.Param.FailedParamInit;
import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.Name;
import jeext.controller.core.param.annotations.composer.ComposeWith;
import jeext.controller.core.param.types.FileType;
import jeext.controller.util.exceptions.UnhandledException;
import jeext.model.Model;
import jeext.util.Parser;
import jeext.util.exceptions.FailedAssertion;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledJeextException;
import jeext.util.exceptions.UnsupportedType;

/**
 * <p>The class that does the heavy lifting
 * for {@link Param} and {@link Composer}
 * <p>Its only job is to retrieve a parameter
 * from an HTTP request and <i>cast</i> it
 * to its appropriate type
 * <p>Most of the methods here and in
 * {@link Composer} are protected so that
 * only {@link Param} and {@link Composer}
 * can use them
 */
public class Retriever {
	
	/**
	 * The name by which to
	 * look for the parameter
	 */
	protected String name;

	/**
	 * <p>The actual type of the parameter.
	 * <p>If its <code>List &lt;Integer&gt;</code>
	 * for example then this {@link Field} would
	 * have the {@link Integer} {@link Class}
	 * in it and not {@link List}
	 */
	protected Class <?> type;
	/**
	 * The multiplicity that this parameter
	 * is expected to be in
	 * 
	 * @see Multiplicity
	 */
	protected Multiplicity multiplicity; 
	
	/**
	 * The kind of this parameter,
	 * to be able to know how to retrieve it
	 * 
	 * @see Kind
	 */
	protected Kind kind;
	
	/**
	 * For {@link Kind#MODEL} type of parameters
	 * only, it specifies the {@link Model}s
	 * ID type
	 */
	protected Class <?> idType;
	/**
	 * For {@link Kind#MODEL} type of parameters
	 * only, it specifies the {@link Model}s
	 * ID kind to be able to know how
	 * to retrieve it
	 * 
	 * @see IDKind
	 */
	protected IDKind idKind;
	/**
	 * For {@link Kind#MODEL} type of parameters
	 * only, it holds the public zero-args
	 * {@link Constructor}
	 */
	protected Constructor <?> constructor;
	/**
	 * For {@link Kind#MODEL} type of parameters
	 * only, it holds an instance
	 * of this {@link Model} to be
	 * able to use {@link Model#clazz}
	 */
	protected Model <?> instance;
	
	/**
	 * The {@link Constructor} called by {@link Param}
	 * to initialize its {@link Retriever}
	 * 
	 * @param parameter
	 * @throws FailedParamInit
	 */
	protected Retriever (Parameter parameter) throws FailedParamInit {
		name = (parameter.isAnnotationPresent(Name.class))? parameter.getAnnotation(Name.class).value() : parameter.getName();
		
		
		type = parameter.getType();
		
		if (type.isArray()) {
			type = type.componentType();
			multiplicity = Multiplicity.ARRAY;
			
		} else if (List.class.equals(type)) {
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

	/**
	 * The {@link Constructor} called by {@link Composer}
	 * to initialize its {@link Retriever}<b>s</b>
	 * 
	 * @param field
	 * @throws FailedParamInit
	 */
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
			
		} else if (List.class.equals(type)) {
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
	
	/**
	 * Processes the {@link #type}
	 * and determines the other values
	 * 
	 * @param owner
	 * @throws FailedParamInit
	 */
	private void processType (Object owner) throws FailedParamInit {
		validateType(owner);
		
		determineKind();
		
		if (kind == Kind.MODEL) {
			validateAndSetModel(owner);
			
			validateIdType(owner);
			
			determineIdKind(owner);
		}
	}

	/**
	 * Validates the {@link #type}
	 * and makes sure its conforms to the
	 * requirements
	 * 
	 * @param owner
	 * @throws FailedParamInit
	 */
	private void validateType (Object owner) throws FailedParamInit {
		if (type.isPrimitive()) {
			throw new FailedParamInit("(" +owner  +") Primitives aren't allowed, use their Object representation instead.");
		}
		
		if (Modifier.isAbstract(type.getModifiers())) {
			throw new FailedParamInit("(" +owner  +") Can't use abstract classes");
		}
		
		if (!isTypeSupported(type)) {
			throw new FailedParamInit("(" +owner  +") Unsuported type `" +type +"`");
		}
	}
	
	/**
	 * Determines the {@link #kind}
	 * from the {@link #type}
	 */
	private void determineKind () {
		if (Model.class.isAssignableFrom(type)) {
			kind = Kind.MODEL;
		} else if (Enum.class.isAssignableFrom(type)) {
			kind = Kind.ENUM;
		} else if (FileType.class.equals(type)) {
			kind = Kind.FILE;
		} else {
			kind = Kind.PRIMITIVE;
		}
	}

	/**
	 * Validates the {@link Model} type
	 * , checks if it conforms with
	 * the requirement and sets the required
	 * information related to the model
	 * 
	 * @param owner
	 * @throws FailedParamInit
	 */
	private void validateAndSetModel (Object owner) throws FailedParamInit {
		if (kind != Kind.MODEL) {
			throw new FailedAssertion("Kind is not model for `" +owner  +"` , instead: " +kind);
		}
		
		constructor = null;
		for (Constructor <?> constructor : type.getDeclaredConstructors()) {
			if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() == 0) {
				this.constructor = constructor;
				break;
			}
		}
		
		if (constructor == null) {
			throw new FailedParamInit("(" +owner  +") Found no public, zero args constructor for the model `" +type +"`");
		}
		

		try {
			instance = (Model <?>) constructor.newInstance();
		} catch (ClassCastException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			throw new UnhandledJeextException(e);
		} catch (InvocationTargetException e) {
			throw new FailedParamInit("(" +owner  +") The empty constructor threw this exception `" +e.getCause() +"` when it was called");
		}
		
		
		List <Field> id = Arrays
				.stream(type.getDeclaredFields())
				.filter((Field field) -> {return field.isAnnotationPresent(MID.class);})
				.toList();
		
		if (id.size() != 1) {
			throw new FailedParamInit("(" +owner  +") The Model (" +type +") has to indentify one single ID field with the `" +MID.class +"` annotation");
		}
		
		this.idType = id.get(0).getType();
	}

	
	/**
	 * Validates the {@link #idType}
	 * 
	 * @param owner
	 * @throws FailedParamInit
	 */
	private void validateIdType(Object owner) throws FailedParamInit {
		if (idType.isPrimitive()) {
			throw new FailedParamInit("(" +owner  +") Primitives can't be used as a models' id, use their Object representation instead");
		}
		
		if (!isIDTypeSupported(idType)) {
			throw new FailedParamInit("(" +owner  +") Unsuported model id type `" +idType +"`");
		}
	}

	/**
	 * Determines the {@link #idKind}
	 * from the {@link #idType}
	 * 
	 * @param owner
	 */
	private void determineIdKind(Object owner) {
		if (kind != Kind.MODEL) {
			throw new FailedAssertion("Kind is not model for `" +owner  +"` , instead: " +kind);
		}
		
		if (Enum.class.isAssignableFrom(idType)) {
			idKind = IDKind.ENUM;
		} else {
			idKind = IDKind.PRIMITIVE;
		}
	}
	
	/**
	 * @param type
	 * @return whether the passed <code>type</code>
	 * is supported by the {@link Retriever}
	 */
	protected static boolean isTypeSupported (Class <?> type) {
		PassedNull.check(type, Class.class);
		
		if (!	(String.class.equals(type) ||
				Number.class.isAssignableFrom(type) ||
				Model.class.isAssignableFrom(type) ||
				Enum.class.isAssignableFrom(type) ||
				FileType.class.equals(type) ||
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
	
	/**
	 * @param idType
	 * @return whether the passed <code>idType</code>
	 * is supported by the {@link Retriever}
	 */
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
	
	/**
	 * The method that actually retrieves the parameter
	 * according to its {@link #kind} and {@link #multiplicity}
	 * 
	 * @param request
	 * @return the retrieved parameter casted to its appropriate type
	 * (if its a {@link List} then a {@link List} is returned)
	 * or <code>null</code>, if it was not found or couldn't be casted
	 */
	protected Object retrieve (HttpServletRequest request) {
		try {
			
			switch (multiplicity) {
			case SINGLE:
				
				switch (kind) {
				case PRIMITIVE:
					return getParameter(name, type, request);
					
				case FILE:
					return getFile(name, request);
					
				case ENUM:
					return getEnum(name, type, request);
					
				case MODEL:
					return getEntity(request);
					
				default:
					throw new UnsupportedType(kind);
				}
				
			case ARRAY:
				
				switch (kind) {
				case PRIMITIVE:
					return getParameters(name, type, request);
					
				case FILE:
					return getFiles(name, request);
					
				case ENUM:
					return getEnums(name, type, request);
					
				case MODEL:
					return getEntities(request);
					
				default:
					throw new UnsupportedType(kind);
				}

			case LIST:
				
				Object array = switch (kind) {
				case PRIMITIVE: 
					yield getParameters(name, type, request);
					
				case FILE:
					yield getFiles(name, request);
					
				case ENUM:
					yield getEnums(name, type, request);
					
				case MODEL:
					yield getEntities(request);
				
				default:
					throw new UnsupportedType(kind);
				};
				
				return constructList(array, type);
				
			default:
				throw new UnsupportedType(multiplicity);
			}
			
		} catch (ClassCastException e) {
			throw new UnhandledJeextException(e);
		}
	}

	/**
	 * @param <T>
	 * @param array
	 * @param type
	 * 
	 * @return a {@link List} from the passed supposed <code>array</code>
	 * or <code>null</code> if the its the <code>array</code> is
	 * <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	private static <T> List <T> constructList (Object array, Class <T> type) {
		if (array == null) {
			return null;
		}
		
		return Arrays.asList((T []) array);
	}
	
	/**
	 * @param request
	 * @return the {@link Model} that was retrieved from
	 * the request
	 */
	private Model <?> getEntity (HttpServletRequest request) {
		Object id;
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
		
		return parseModel(id);
	}
	
	/**
	 * @param request
	 * @return an {@link Array} of {@link Model}s retrieved
	 * from the request
	 */
	private Model <?> [] getEntities (HttpServletRequest request) {
		Object [] ids;
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
		
		Model <?> [] entities = new Model <?> [ids.length];
		for (int i = 0; i < ids.length; i++) {
			entities[i] = parseModel(ids[i]);
		}
		
		return entities;
	}
	
	/**
	 * @param id
	 * @return the {@link Model}
	 * that has the passed <code>id</code>
	 */
	private Model <?> parseModel (Object id) {
		if (id == null) {
			return null;
		}
		
		return instance.clazz.find(id);
	}
	
	/**
	 * @param <T>
	 * @param name
	 * @param type
	 * @param request
	 * @return the {@link Enum} from the request
	 */
	private static <T> T getEnum (String name, Class <T> type, HttpServletRequest request) {
		String constant = getParameter(name, String.class, request);
		return parseEnum(constant, type);
	}
	
	/**
	 * @param <T>
	 * @param name
	 * @param type
	 * @param request
	 * @return an {@link Array} of {@link Enum}s from
	 * the request
	 */
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
	
	/**
	 * @param <T>
	 * @param <S>
	 * @param constant
	 * @param type
	 * 
	 * @return the {@link Enum} constant of the
	 * {@link Enum} class <code>type</code> that has the same name
	 * as the passed <code>constant</code>, or <code>null</code>
	 * if there is none matching
	 */
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
			throw new UnhandledJeextException(e);
		}
	}
	
	/**
	 * @param name
	 * @param request
	 * 
	 * @return {@link FileType} from the requests part
	 */
	private static FileType getFile (String name, HttpServletRequest request) {
		if (!isMultipart(request)) {
			return null;
		}
		
		try {
			Part part = request.getPart(name);
			if (part == null) {
				return null;
			}
			
			return new FileType(part);
			
		} catch (IOException e) { // FileType and getPart
			return null;
			
		} catch (ServletException | IllegalStateException e) { // getPart
			throw new UnhandledException(e);
		}
	}
	
	/**
	 * @param name
	 * @param request
	 * 
	 * @return an {@link Array} of {@link FileType}s
	 * from the requests parts
	 */
	private static FileType [] getFiles (String name, HttpServletRequest request) {
		if (!isMultipart(request)) {
			return null;
		}
		
		try {
			ArrayList <FileType> files = new ArrayList <> ();
			
			for (Part part : request.getParts()) {
				if (name.equals(part.getName())) {
					try {
						files.add(new FileType(part));
					} catch (IOException e) { // FileType
						files.add(null);
					}
				}
			}
			
			return files.toArray(new FileType [files.size()]);
			
		} catch (IOException e) { // getParts
			return null;
			
		} catch (ServletException | IllegalStateException e) {
			throw new UnhandledException(e);
		}
	}
	
	/**
	 * In case i don't use {@link ServletFileUpload} sometime in the future
	 * 
	 * @param request
	 * @return whether the request is a multipart request or not
	 */
	private static boolean isMultipart (HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}
	
	/**
	 * @param <T>
	 * @param name
	 * @param type
	 * @param request
	 * 
	 * @return the primitive parameter from the request
	 */
	private static <T> T getParameter (String name, Class <T> type, HttpServletRequest request) {
		String parameter = request.getParameter(name);
		return parse(parameter, type);
	}

	/**
	 * @param <T>
	 * @param name
	 * @param type
	 * @param request
	 * 
	 * @return an {@link Array} of primitive parameters from the request
	 */
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
	
	/**
	 * @param <T>
	 * @param s
	 * @param type
	 * 
	 * @return the {@link String} <code>s</code> parsed
	 * to the specified type <code>type</code>
	 */
	protected static <T> T parse (String s, Class <T> type) {
		try {
			return Parser.parse(s, type);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new UnsupportedType(type);
		}
	}
	
	/**
	 * An {@link Enum} to specify the
	 * multiplicity in which this parameter
	 * is expected
	 */
	protected static enum Multiplicity {
		/**
		 * Simple parameter
		 */
		SINGLE,
		/**
		 * Multiple parameters casted to an {@link Array}
		 */
		ARRAY,
		/**
		 * Multiple parameters casted to a {@link List}
		 */
		LIST;
	}
	
	/**
	 * An {@link Enum} to determine the
	 * kind of the parameter and thus
	 * know how to retrieve it properly
	 */
	protected static enum Kind {
		/**
		 * Primitive means anything
		 * other than the rest of the constants
		 * here, and not the primitives themselves (int, float ..)
		 */
		PRIMITIVE,
		/**
		 * {@link FileType} type of parameters
		 */
		FILE,
		/**
		 * {@link Enum} type of parameters
		 */
		ENUM,
		/**
		 * {@link Model} type of parameters
		 */
		MODEL;
	}
	
	/**
	 * For {@link Kind#MODEL} parameters only,
	 * to specify the type of the {@link Model}s id
	 */
	protected static enum IDKind {
		/**
		 * Normal types of ID
		 */
		PRIMITIVE,
		/**
		 * {@link Enum} type of ID
		 */
		ENUM;
	}

	@Override
	public String toString() {
		return "Retriever [name=" + name + ", type=" + type + ", multiplicity=" + multiplicity + ", kind=" + kind
				+ ", idType=" + idType + ", idKind=" + idKind + ", constructor=" + constructor + ", instance="
				+ instance + "]";
	}

}
