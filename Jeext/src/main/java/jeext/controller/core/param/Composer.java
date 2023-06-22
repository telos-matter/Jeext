package jeext.controller.core.param;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.param.Param.FailedParamInit;
import jeext.controller.core.param.Retriever.Kind;
import jeext.controller.core.param.Retriever.Multiplicity;
import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.composer.ComposeWith;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.annotations.composer.Ignore;
import jeext.util.Strings;
import jeext.util.exceptions.FailedAssertion;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledJeextException;
import jeext.util.exceptions.UnsupportedType;

public class Composer {

	private Retriever retriever; // CONSIDER you are not necessarily going to find the id
	private FieldRetriever [] fieldRetrievers;
	
	private boolean requireAll;
	private boolean retrieveFirst; //MENTION will expect the id, and fail if not found
	// MENTION need public 0 arg constr
	
	protected Composer (Parameter parameter) throws FailedParamInit {
		retriever = new Retriever(parameter);
		
		if (retriever.kind != Kind.MODEL) {
			throw new FailedParamInit("Can only use `" +Composed.class +"` with model type of Params");
		}
		
		
		Composed composed = parameter.getAnnotation(Composed.class);
		requireAll = composed.requireAll();
		retrieveFirst = composed.retrieveFirst();
		
		List <FieldRetriever> _fieldRetrievers = new ArrayList <> ();
		for (Field field : retriever.type.getDeclaredFields()) {
			if (field.isAnnotationPresent(Ignore.class) && field.isAnnotationPresent(ComposeWith.class)) {
				throw new FailedParamInit("This field `" +field +"` has both the `" +Ignore.class +"` and `" +ComposeWith.class +"` annotations, choose one.");
			}
			
			if (field.isAnnotationPresent(Ignore.class)) {
				continue;
			}
			
			try {
				FieldRetriever fieldRetriever = new FieldRetriever(retriever.type, field);
				
				if (field.isAnnotationPresent(MID.class)) {
					if (composed.ignoreID()) {
						continue;
					}
					
					fieldRetriever.retriever.name = retriever.name;
				}
				
				_fieldRetrievers.add(fieldRetriever);
				
			} catch (FailedParamInit e) {
				if (field.isAnnotationPresent(ComposeWith.class)) {
					throw e;
				} else {
					continue;
				}
			}
		}
		
		fieldRetrievers = _fieldRetrievers.toArray(new FieldRetriever [_fieldRetrievers.size()]);
	}
	
	protected Object compose (HttpServletRequest request) throws InvocationTargetException, InvalidParameter {
		Object model = null;
		
		if (retrieveFirst) {
			if ((model = retriever.retrieve(request)) == null) {
				throw new InvalidParameter("RetrieveFirst is set, and couldn't retrieve\n" +retriever);
			}
		} else {
			try {
				model = retriever.constructor.newInstance(null);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new UnhandledJeextException(e);
			}
		}
	
		if (requireAll) {
			for (int i = 0; i < fieldRetrievers.length; i++) {
				if (!fieldRetrievers[i].retrieveField(model, request)) {
					throw new InvalidParameter("RequireAll is set, and missed this field `" +fieldRetrievers[i].field +"`");
				}
			}
			
		} else {
			for (int i = 0; i < fieldRetrievers.length; i++) {
				fieldRetrievers[i].retrieveField(model, request);
			}
		}
		
		return model;
	}
	
	// Only job is to retrieve and put in a field, not the retrieveFirst
	private static class FieldRetriever {
		private Field field;
		private Retriever retriever;
		
		private Method setter;
		
		// MENTION setter should ofc be public static and kda, if exception is thrown, up to you
		// does not check if not ignored
		public FieldRetriever (Class <?> clazz, Field field) throws FailedParamInit {
			this.field = field;
			
			retriever = new Retriever(field);
			
			if (field.isAnnotationPresent(ComposeWith.class)) {
				ComposeWith composeWith = field.getAnnotation(ComposeWith.class);
				
				if (composeWith.useSetter()) {
					String setterMethod = (composeWith.setterMethod().isBlank())? "set" +Strings.capitalize(field.getName()) : composeWith.setterMethod();
					setter = getSetterMethod(clazz, retriever, setterMethod);
					if (setter == null) {
						throw new FailedParamInit("Found no appropriate setter method (public, non-static, void returning and takes one parameter that is the same type as the field) under the name `" +setterMethod +"` for the field `" +field +"`");
					}
					
				} else {
					setter = null;
				}
				
			} else {
				String setterMethod = "set" +Strings.capitalize(field.getName());
				setter = getSetterMethod(clazz, retriever, setterMethod);
			}
			
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				throw new FailedParamInit("Can't compose with this field `" + field+"` because its static or final ");
			}
			if (setter == null && !Modifier.isPublic(modifiers)) {
				throw new FailedParamInit("Can't compose with this field `" +field +"` because it is not public and no appropriate setter method was found / defined. Use the `" +ComposeWith.class +"` annotation to define a setter method, or make the field public.");
			}
		}
		
		private boolean retrieveField (Object model, HttpServletRequest request) throws InvocationTargetException {
			PassedNull.check(model, Object.class);
			
			Object value = retriever.retrieve(request);
			if (value == null) {
				return false;
			}
			
			try {
				
				if (setter == null) {
					field.set(model, value);
				} else {
					setter.invoke(model, value);
				}
				return true;
				
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new UnhandledJeextException(e);
			}
		}
		
		/**
		 * <p>Looks for the public, non-static and void returning 
		 * setter {@link Method}
		 * from the specified {@link Class} with the specified <code>name</code>
		 * that takes the specified <code>type</code> as its only
		 * {@link Parameter}
		 * <p>The <code>type</code>s {@link Multiplicity}
		 * is specified with the <code>multiplicity</code> parameter
		 * and if it is anything other than {@link Multiplicity#SINGLE}
		 * then the <code>genericType</code> should be specified
		 * <p><b>Keep in mind that</b> the {@link Retriever#type}
		 * is not the type that the setter method is taking as its
		 * parameter in the case of {@link Multiplicity#LIST} for example.
		 * Instead it takes {@link List} as its parameter and {@link Retriever#type}
		 * is the <code>genericType</code>
		 * @return the {@link Method} that satisfies the conditions
		 * or <code>null</code> if there was none matching
		 * <p>Fuck that shit, gimme the damn retriever, name and clazz
		 */
		private static Method getSetterMethod (Class <?> clazz, Retriever retriever, String name) {
			try {
				Method method;
				
				switch(retriever.multiplicity) {
				case SINGLE:
				{
					method = clazz.getDeclaredMethod(name, retriever.type);
					break;
				}
				case ARRAY:
				{
					Class <?> type = retriever.type.arrayType();
					method = clazz.getDeclaredMethod(name, type);
					break;
				}
				case LIST:
				{
					method = clazz.getDeclaredMethod(name, List.class);
					Type genericType = method.getParameters()[0].getParameterizedType();
					if (genericType instanceof ParameterizedType parameterizedType) {
						Class <?> type = (Class <?>) parameterizedType.getActualTypeArguments()[0];
						if (!type.equals(retriever.type)) {
							return null;
						}
					} else {
						throw new UnsupportedType(genericType);
					}
					break;
				}
				default:
					throw new UnsupportedType(retriever.multiplicity);
				}
				
				int modifiers = method.getModifiers();
				if (!Modifier.isPublic(modifiers) ||
					Modifier.isStatic(modifiers) ||
					!method.getReturnType().equals(void.class)) {
					return null;
					
				} else {
					return method;
				}
			} catch (NoSuchMethodException e) {
				return null;
			} catch (SecurityException e) {
				throw new UnhandledJeextException(e);
			}
		}
	}
	
}
