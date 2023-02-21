package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import models.core.Model;
import util.exceptions.FailedInit;
import util.exceptions.UnhandledException;

/**
 * A class that facilitates working with {@link Model} typed {@link Collection}s.
 */
public class Collections {
		
	/**
	 * Checks if the given {@link Collection} contains
	 * the given {@link Model} by comparing the ids
	 * @return the {@link Model} from the
	 * given {@link Collection} if found, otherwise <code>null</code>
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 */
	public static <T extends Model <T>> T contains (Collection <T> collection, T model) {
		Objects.requireNonNull(collection);
		Objects.requireNonNull(model);
		
		for (T collection_model : collection) {
			if (collection_model == null) {
				continue;
			}
			if (model.getId() == collection_model.getId()) {
				return collection_model;
			}
		}
		
		return null;
	}
	
	/**
	 * Removes the given {@link Model} from the given {@link Collection}
	 * based on the id
	 * @return <code>true</code> if the given {@link Model} was found
	 * in the given {@link Collection} and it was removed, otherwise <code>false</code>
	 * @see #contains(Collection, Model)
	 */
	public static <T extends Model <T>> boolean remove (Collection <T> collection, T model) {
		T collection_model = contains(collection, model);
		
		return (collection_model == null) ? false : collection.remove(collection_model);
	}
	
	/**
	 * Adds the given {@link Model} to the given {@link Collection}
	 * if and only if it doesn't already exist in it based on the id
	 * @return <code>true</code> if it was added, <code>false</code> otherwise
	 * @see #contains(Collection, Model)
	 */
	public static <T extends Model <T>> boolean addAsSet (Collection <T> collection, T model) {
		if (contains(collection,model) == null) {
			return collection.add(model);
		} else {
			return false;
		}
	}

	/**
	 * @return <code>true</code> if the given {@link Collection} contains the given
	 * {@link Model} based on the id, <code>false</code> otherwise
	 * @see #contains(Collection, Model)
	 */
	public static <T extends Model <T>> boolean boolContains (Collection <T> collection, T model) {
		return contains(collection, model) != null;
	}

	/**
	 * Negates {@link #boolContains(Collection, Model)}
	 */
	public static <T extends Model <T>> boolean boolNotContains (Collection <T> collection, T model) {
		return ! boolContains(collection, model);
	}
	
	/**
	 * @param collection	of an object that contains the model
	 * @param model	to be searched for by id
	 * @param get_method	the methods' name that returns the
	 *  model form the collections' object
	 * @return the collection object that contains the model
	 * 
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 * @throws InvocationTargetException wrapped inside a {@link RuntimeException} if
	 * the specified method throws an exception
	 * @throws NoSuchMethodException wrapped inside a {@link RuntimeException} if the
	 * given get_method {@link String} doesn't specify any existing method
	 * @throws SecurityException wrapped inside a {@link RuntimeException} if the 
	 * specified methods' class throws it
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model<T>, V> V containsWithin (Collection <V> collection, T model, String get_method) {
		Objects.requireNonNull(collection);
		Objects.requireNonNull(model);
		Objects.requireNonNull(get_method);
		
		if (collection.size() == 0) {
			return null;
		}
		
		Class <?> clazz = collection.iterator().next().getClass();
		
		try {
			Method method = clazz.getDeclaredMethod(get_method, null);
			
			int modifiers = method.getModifiers();
			if ((!Modifier.isPublic(modifiers)) || Modifier.isStatic(modifiers)) {
				throw new RuntimeException("The given method '" +get_method +"' of the class " +clazz +" must be public and non-static");
			}
			// TODO use failed requirements
			
			if (!method.getReturnType().equals(model.getClass())) {
				throw new RuntimeException("The return type of the given method '" +get_method +"' of the class " +clazz +" must match that of the given model: " +model.getClass());
			}

			if (method.getParameterCount() != 0) {
				throw new RuntimeException("The given method '" +get_method +"' of the class " +clazz +" must take no parameters");
			}
			
			try {
				
				for (V element : collection) {
					if (element == null) {
						continue;
					}
					if (model.equalsId((T) method.invoke(element, null))) {
						return element;
					}
				}
				
				return null;
				
			} catch (IllegalAccessException | IllegalArgumentException e) {
				throw new UnhandledException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Same as ContainsWithin with the get_method set to 
	 * the "get<i>models_class_name</i>"
	 */
	public static <T extends Model<T>, V> V containsWithin (Collection <V> collection, T model) {
		return containsWithin(collection, model, "get" +model.getClass().getSimpleName());
	}
	
	public static <T extends Model <T>, V> boolean removeWithin (Collection <V> collection, T model) {
		V collection_model = containsWithin(collection, model);
		
		return (collection_model == null) ? false : collection.remove(collection_model);
	}
	
	/**
	 * Creates a {@link List} that is the intersection of
	 * the given a {@link Collection} and the given b {@link Collection}
	 * based on the ids of the {@link Model}s
	 * @return a {@link List} composed of the elements of the 
	 * given a {@link Collection} if they also exist 
	 * in the given b {@link Collection}
	 * @see #contains(Collection, Model)
	 */
	public static <T extends Model <T>> List <T> intersection (Collection <T> a, Collection <T> b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);
		
		List <T> list = new LinkedList <> ();
		
		for (T model : a) {
			if (model == null) {
				continue;
			}
			if (boolContains(b, model) && boolNotContains(list, model)) {
				list.add(model);
			}
		}
		
		return list;
	}
	
}
