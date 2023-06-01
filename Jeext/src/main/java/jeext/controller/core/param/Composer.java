package jeext.controller.core.param;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jeext.controller.core.param.Param.FailedParamInit;
import jeext.controller.core.param.Retriever.IDKind;
import jeext.controller.core.param.Retriever.Kind;
import jeext.controller.core.param.annotations.composer.ComposeWith;
import jeext.controller.core.param.annotations.composer.Composed;
import jeext.controller.core.param.annotations.composer.Ignore;
import jeext.controller.util.exceptions.UnhandledException;
import jeext.util.Strings;
import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnhandledDevException;

public class Composer {

	private Retriever retriever; // CONSIDER you are not necessarily going to find the id
	private FieldRetriever [] fieldRetrievers;
	
	private boolean requireAll;
	private boolean retrieveFirst;
	
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

	// also, what about the id field? just ignore it auto?
	protected Object compose (HttpServletRequest request) {
		Object model = null;
		// is it ever going to return a non init model? like is it ever going to return null?
		if (requireAll) {
			
		} else {
			// just to test
			try {
				model = retriever.type.getDeclaredConstructors()[0].newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
			
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
		
		// TODO check if i receive static fields
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
						throw new FailedParamInit("Found no appropriate setter method under the name `" +setterMethod +"` for `" +field +"`");
					}
					
				} else {
					setter = null;
				}
				
			} else {
				String setterMethod = "set" +Strings.capitalize(field.getName());
				setter = getSetterMethod(clazz, retriever.type, setterMethod);
			}
		}
		
		private boolean retrieveField (Object model, HttpServletRequest request) {
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
				throw new UnhandledDevException(e);
			} catch (InvocationTargetException e) {
				throw new UnhandledException(e);
			}
		}
		
		/*
		 * Unhandled exception: java.lang.IllegalAccessException: class jeext.controller.core.param.Composer$FieldRetriever cannot access a member of class controllers.TestModel with modifiers "private"
FOR ASSERTION, IF YOU SEE THIS EXCEPTION PLEASE CONTACT THE JEEXT FRAMEWORK DEV
		*/
		
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
				throw new UnhandledDevException(e);
			}
		}
		
	}
	
}
