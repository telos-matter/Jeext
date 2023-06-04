package jeext.controller.core.param;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jeext.controller.core.exceptions.InvalidParameter;
import jeext.controller.core.param.Param.FailedParamInit;
import jeext.controller.core.param.Retriever.Kind;
import jeext.controller.core.param.annotations.composer.ComposeWith;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.annotations.composer.Ignore;
import jeext.util.Strings;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledJeextException;

public class Composer {

	private Constructor <?> constructor;
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
		
		for (Constructor <?> constructor : retriever.type.getDeclaredConstructors()) {
			if (Modifier.isPublic(constructor.getModifiers()) && constructor.getParameterCount() == 0) {
				this.constructor = constructor;
				break;
			}
		}
		
		if (constructor == null) {
			throw new FailedParamInit("Found no public, zero args constructor for the model `" +retriever.type +"`");
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
				_fieldRetrievers.add(new FieldRetriever(retriever.type, field));
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
	// CONSIDER do i need retriever if not retrieve first? keep in mind it does the checking, makhasr walo
	// also, what about the id field? just ignore it auto? No just like any other field, compose
	protected Object compose (HttpServletRequest request) throws InvocationTargetException, InvalidParameter {
		Object model = null;
		
		if (retrieveFirst) {
			if ((model = retriever.retrieve(request)) == null) {
				throw new InvalidParameter("RetrieveFirst is set, and couldn't retrieve\n" +retriever);
			}
		} else {
			try {
				model = constructor.newInstance(null);
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
					setter = getSetterMethod(clazz, retriever.type, setterMethod);
					if (setter == null) {
						throw new FailedParamInit("Found no appropriate setter method (public, non-static, void returning and takes one parameter that is the same type as the field) under the name `" +setterMethod +"` for the field `" +field +"`");
					}
					
				} else {
					setter = null;
				}
				
			} else {
				String setterMethod = "set" +Strings.capitalize(field.getName());
				setter = getSetterMethod(clazz, retriever.type, setterMethod);
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
		 * @return the public, non-static and void returning setter {@link Method}
		 * from the specified {@link Class} with the specified <code>name</code>
		 * that takes the specified <code>type</code> as its only
		 * {@link Parameter}, or <code>null</code> if there is none matching
		 * the description
		 */
		private static Method getSetterMethod (Class <?> clazz, Class <?> type, String name) {
			try {
				Method method = clazz.getDeclaredMethod(name, type);

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
