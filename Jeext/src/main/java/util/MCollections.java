package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import models.User;
import models.core.Model;
import util.exceptions.FailedRequirement;
import util.exceptions.UnhandledException;

/**
 * <p>A class that facilitates working with {@link Model} typed {@link Collection}s.
 * <p>Unlike other utility classes, this one does NOT tolerate <code>null</code>.
 * <p>Note: The class is named
 * MCollections (M for {@link Model}) to avoid mix-ups with
 * the Java {@link Collections} class that also provides utilities
 * for {@link Collection}s
 */
public class MCollections {
		
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
	 * Given a class which contains/has access to
	 * a {@link Model}, this method checks
	 * if a {@link Collection} of said class contains the given
	 * {@link Model} by retrieving the {@link Model} from
	 * the {@link Collection}s' elements  and comparing their ids
	 * 
	 * @param collection	of elements that contain/have access to a {@link Model}
	 * @param model	to be searched for by id within the {@link Collection}s' elements
	 * @param get_method	the name of the method that returns the 
	 * {@link Model} from the collections' elements.
	 * This method should be public, non-static, takes no arguments
	 * and ,of course, has a
	 * return type that matches the type of the given {@link Model}. If
	 * the get_method is <code>null</code>, it is set to
	 * "get<i>models_class_name</i>"; For example
	 * if the given {@link Model} is of type {@link User}, 
	 * the get_method is set to "getUser" 
	 * 
	 * @return the {@link Collection}s' element that
	 * contains the {@link Model} if found, or <code>null</code> if not
	 * 
	 * @throws NullPointerException if 
	 * either of collection or model are <code>null</code>
	 * @throws FailedRequirement if the specified method is any of the following:
	 * <ul>
	 * <li>not public</li> 
	 * <li>static</li> 
	 * <li>takes arguments</li> 
	 * <li>the return type is different than that of the given {@link Model}</li> 
	 * </ul>
	 * @throws InvocationTargetException wrapped inside a {@link RuntimeException} if
	 * the specified method throws an exception
	 * @throws NoSuchMethodException wrapped inside a {@link RuntimeException} if the
	 * given get_method {@link String} doesn't specify any existing method
	 * @throws SecurityException wrapped inside a {@link RuntimeException} if the 
	 * specified method throws it
	 */
	public static <T extends Model<T>, V> V containsWithin (Collection <V> collection, T model, String get_method) {
		Objects.requireNonNull(collection);
		Objects.requireNonNull(model);
		
		if (collection.size() == 0) {
			return null;
		}
		
		if (get_method == null) {
			get_method = "get" +model.getClass().getSimpleName();
		}
		
		Class <?> clazz = collection.iterator().next().getClass();
		
		try {
			Method method = clazz.getDeclaredMethod(get_method, null);
			
			int modifiers = method.getModifiers();
			if ((!Modifier.isPublic(modifiers)) || Modifier.isStatic(modifiers)) {
				throw new FailedRequirement("The given method '" +get_method +"' of the class " +clazz +" must be public and non-static");
			}
			
			if (!method.getReturnType().equals(model.getClass())) {
				throw new FailedRequirement("The return type of the given method '" +get_method +"' of the class " +clazz +" must match that of the given model: " +model.getClass());
			}

			if (method.getParameterCount() != 0) {
				throw new FailedRequirement("The given method '" +get_method +"' of the class " +clazz +" must take no parameters");
			}
			
			try {
				
				for (V element : collection) {
					if (element == null) {
						continue;
					}
					
					@SuppressWarnings("unchecked")
					T collection_model = (T) method.invoke(element, null);
					if (model.equalsId(collection_model)) {
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
	 * <p>Same principal as {@link #remove(Collection, Model)}, but removes
	 * the {@link Collection}s' element that contains the {@link Model}
	 * <p>Basically, uses {@link #containsWithin(Collection, Model, String)} instead
	 * of {@link #contains(Collection, Model)}
	 * @see #containsWithin(Collection, Model, String)
	 * @see #remove(Collection, Model)
	 * @see #contains(Collection, Model)
	 */
	public static <T extends Model <T>, V> boolean removeWithin (Collection <V> collection, T model, String get_method) {
		V collection_model = containsWithin(collection, model, get_method);
		
		return (collection_model == null) ? false : collection.remove(collection_model);
	}

	/**
	 * Same principle as {@link #boolContains(Collection, Model)}, but uses
	 * {@link #containsWithin(Collection, Model, String)} instead of
	 * {@link #contains(Collection, Model)}
	 * @see #containsWithin(Collection, Model, String)
	 * @see #boolContains(Collection, Model)
	 * @see #contains(Collection, Model)
	 */
	public static <T extends Model <T>, V> boolean boolContainsWithin (Collection <V> collection, T model, String get_method) {
		return containsWithin(collection, model, get_method) != null;
	}

	/**
	 * Negates {@link #boolContainsWithin(Collection, Model)}
	 */
	public static <T extends Model <T>, V> boolean boolNotContainsWithin (Collection <V> collection, T model, String get_method) {
		return ! boolContainsWithin(collection, model, get_method);
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
