package util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import models.core.Model;

public class Collections {
		
	/**
	 * @param collection	of an object that contains the model
	 * @param model	to be searched for by id
	 * @param get_method	the methods' name that returns the
	 *  model form the collections' object
	 * @return the collection object that contains the model
	 */
	// TODO: better handle the exceptions
	// TODO: public non static and valid return type
	// TODO: refactor the exceptions as to use fewer
	// TODO: clean up this whole class and finish the other within methods
	@SuppressWarnings("unchecked")
	public static <T extends Model<T>, V> V containsWithin (Collection <V> collection, T model, String get_method) {
		if (collection.size() == 0) {
			return null;
		}
		
		Class <?> clazz = collection.iterator().next().getClass();
		try {
			Method method = clazz.getDeclaredMethod(get_method, null);
			
			for (V element : collection) {
				if (model.equalsId((T) method.invoke(element, null))) {
					return element;
				}
			}
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// method exception
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// method exception
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// method exception
			e.printStackTrace();
		}
		
		return null;
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
	 * @return A {@link List} composed of the elements in the a collection
	 * that also exist in the b collection
	 */
	public static <T extends Model <T>> List <T> intersection (Collection <T> a, Collection <T> b) {
		List <T> list = new LinkedList <> ();
		for (T a_model : a) {
			for (T b_model : b) {
				if ((a_model.getId() == b_model.getId()) && boolNotContains(list, a_model)) {
					list.add(a_model);
				}
			}
		}
		
		return list;
	}

	/**
	 * boolean result of {@link #contains(Collection, Model)}
	 */
	public static <T extends Model <T>> boolean boolContains (Collection <T> collection, T model) {
		return contains(collection, model) != null;
	}

	/**
	 * boolean result of {@link #contains(Collection, Model)}
	 */
	public static <T extends Model <T>> boolean boolNotContains (Collection <T> collection, T model) {
		return ! boolContains(collection, model);
	}

	/**
	 * <p>Checks whether or not the model is within the collection
	 * by comparing the ids
	 * @return The model from the passed collection if found, otherwise null
	 */
	public static <T extends Model <T>> T contains (Collection <T> collection, T model) {
		for (T collection_model : collection) {
			if (model.getId() == collection_model.getId()) {
				return collection_model;
			}
		}
		
		return null;
	}
	
	/**
	 * <p>Removes the model from the collection based on the id
	 * <p>Uses the {@link #contains(Collection, Model)} method
	 */
	public static <T extends Model <T>> boolean remove (Collection <T> collection, T model) {
		T collection_model = contains(collection, model);
		
		return (collection_model == null) ? false : collection.remove(collection_model);
	}
	
	/**
	 * <p>Adds the model if and only if it doesn't already
	 *exist in the collection based on the id
	 * <p>Uses the {@link #contains(Collection, Model)} method
	 */
	public static <T extends Model <T>> boolean addAsSet (Collection <T> collection, T model) {
		if (contains(collection,model) == null) {
			return collection.add(model);
		}
		
		return false;
	}
	
}
