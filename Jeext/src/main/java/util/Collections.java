package util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import models.core.Model;

public class Collections {
	
	/**
	 * @return The intersection formed by the two collections
	 * based on the ids.
	 * @apiNote 
	 * The elements of the returned list are form the a collection
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
	 * Check {@link #contains(Collection, Model)}
	 */
	public static <T extends Model <T>> boolean boolContains (Collection <T> collection, T model) {
		return contains(collection, model) != null;
	}

	/**
	 * Check {@link #contains(Collection, Model)}
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
