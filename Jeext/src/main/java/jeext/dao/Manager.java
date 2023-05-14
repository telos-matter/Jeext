package jeext.dao;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import jeext.util.exceptions.PassedNull;
import jeext.util.exceptions.UnsupportedType;

/**
 * A class that facilitates using the {@link EntityManager}
 * to perform CRUD operations as well as some other SQL queries
 */
public class Manager {

	/**
	 * If set to <code>true</code>, the methods will
	 * print, trough {@link System#out}, information
	 * about the queries being performed. otherwise
	 * they'll be silent
	 */
	private static boolean DEBUG_MODE = true;
	
	/**
	 * @return the entity with the given key in
	 * the specified table (clazz). Or <code>null</code>
	 * if the entity doesn't exist or an exception occurred
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 */
	public static <T> T find (Class <T> clazz, Object key) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(key);
		
		if (DEBUG_MODE) {
			System.out.println("Finding: " +key +" from the class: " +clazz);
		}
		
        EntityManager entityManager = null;
        T entity = null;
        
        try {
        	entityManager = Factory.getFactory().createEntityManager();
        	entity = entityManager.find(clazz, key); 
        	
        } catch (Exception e) {
        	System.err.println("Exception while finding: " +key +" from the class: " +clazz);
            e.printStackTrace();
            
        } finally {
        	if (entityManager != null) {
        		entityManager.close();
        	}
        }
        
		if (DEBUG_MODE) {
			System.out.println("Found: " + entity);
		}
		
		return entity;
	}
	
	/**
	 * <p>Inserts the given entity into it's corresponding
	 * table in the DB
	 * <p>Note that it matters how you configure your
	 * {@link Id} column here in the java class and in 
	 * the DB
	 * @return <code>true</code> if all went well, <code>false</code>
	 * if not
	 * @throws NullPointerException if entity is <code>null</code>
	 */
	public static boolean insert (Object entity) {
		Objects.requireNonNull(entity);
		
		if (DEBUG_MODE) {
			System.out.println("Inserting: " + entity);
		}
		
		boolean isSuccessful = performOperation(Operation.PERSIST, entity, null, null);
        
        if (DEBUG_MODE) {
        	System.out.println("After inserting: " + entity);
        }
        
        return isSuccessful;
	}
	
	/**
	 * Updates the given entity (which is 
	 * identified by its ID in its class) in the DB
	 * @return <code>true</code> if all went well, <code>false</code>
	 * if not
	 * @throws NullPointerException if entity is <code>null</code>
	 */
	public static boolean update (Object entity) {
		Objects.requireNonNull(entity);
		
		if (DEBUG_MODE) {
			System.out.println("Updating: " + entity);
		}
		
		boolean isSuccessful = performOperation(Operation.MERGE, entity, null, null);
        
		if (DEBUG_MODE) {
			System.out.println("After updating: " + entity);
		}
		
		return isSuccessful;
	}
	
	/**
	 * <p>Deletes the specified entity from the DB. (An
	 * entity is identified by its ID and its table, AKA its class)
	 * <p>Note that in debug mode, it is checked whether the entity was
	 * really removed or not. This can happen (the specified entity
	 * not being deleted) if another entity depends on
	 * the specified one.
	 * @return <code>true</code> if all went well, <code>false</code>
	 * if not
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 */
	public static boolean delete (Class <?> clazz, Object key) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(key);
		
		if (DEBUG_MODE) {
			System.out.println("Deleting: " + key +" from the class: " +clazz);
		}
		
		boolean isSuccessful = performOperation(Operation.REMOVE, null, clazz, key);
        
		if (DEBUG_MODE) {
			if (find(clazz, key) == null) {
				System.out.println("Deleted succesfully.");
			} else {
				isSuccessful = false;
				System.out.println("/!\\ " +key +" from the class: " +clazz +"was NOT deleted.");
			}
		}
		
		return isSuccessful;
	}
	
	/**
	 * <p>Helper method to remove the boilerplate code from
	 * the update CRUD operations (CUD)
	 * <p>In case the operation is {@link Operation#REMOVE} the clazz
	 * and key should be passed instead of the entity
	 * @return <code>true</code> if everything went well and the operation
	 * was successful, <code>false</code> otherwise
	 */
	private static boolean performOperation (Operation operation, Object entity, Class <?> clazz, Object key) {
		PassedNull.check(operation, Operation.class);
		if (operation == Operation.REMOVE) {
			PassedNull.check(clazz, Class.class);
			PassedNull.check(key, Object.class);
		} else {
			PassedNull.check(entity, Object.class);
		}

		EntityManager entityManager = null;
		EntityTransaction entityTransaction = null;

        try {
        	entityManager = Factory.createEntityManager();
        	entityTransaction = entityManager.getTransaction();
        	
        	entityTransaction.begin();
        	
        	switch (operation) {
        	case PERSIST:
				entityManager.persist(entity);
				break;
        	case MERGE:
				entityManager.merge(entity);
				break;
        	case REMOVE:
        		entityManager.remove(entityManager.find(clazz, key));
				break;
			default:
				throw new UnsupportedType(operation);
			}
            
            entityTransaction.commit();
            
            return true;
            
        } catch (UnsupportedType e) {
        	throw e;
        	
		} catch (Exception  e) {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            
            System.err.println("Exception while preforming: " +operation +" operation on entity: " +entity +", clazz: " +clazz +", key: " +key);
            e.printStackTrace();
            
            return false;
            
        } finally {
        	if (entityManager != null) {
        		entityManager.close();
        	}
        }
	}
	
	/**
	 * Selects all entities in the specified table
	 * @return a {@link List} of all entities of the
	 * specified table, or <code>null</code> if an
	 * exception occurred
	 * @throws NullPointerException if clazz is <code>null</code>
	 */
	public static <T> List <T> selectAll (Class <T> clazz) {
		Objects.requireNonNull(clazz);
		
		if (DEBUG_MODE) {
    		System.out.println("Selecting all from the class: " + clazz);
		}
		
		String query = "SELECT c FROM "+clazz.getName()+" c";
		
		EntityManager entityManager = null;
    	List <T> list = null;
    	
		try {
			entityManager = Factory.getFactory().createEntityManager();
			TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    		list = typedQuery.getResultList();
    		
    	} catch(Exception e) {
    		System.err.println("Exception while selecting all from the class: " +clazz);
    		e.printStackTrace();
    		
    	} finally {
    		if (entityManager != null) {
    			entityManager.close();
    		}
    	}
    	
    	if (DEBUG_MODE) {
			System.out.println("Selected all: " + list);
		}
    	
    	return list;
	}
	
	/**
	 * Selects the unique entity, in the specified table, which
	 * has the given field (name in the Java class) set to the given value
	 * @return the unique entity that satisfies the requirements, or
	 * <code>null</code> if it doesn't exist or an exception occurred
	 * @throws NullPointerException if clazz or field is <code>null</code>
	 */
	public static <T> T selectUnique (Class <T> clazz, String field, Object value) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(field);
		
		if (DEBUG_MODE) {
			System.out.println("Selecting unique: "  +field +" with the value: " +value +" from the class: " +clazz);
		}
		
		String query = "SELECT c FROM " +clazz.getName() +" c WHERE c." +field +" = :value";
		
		EntityManager entityManager = null;
    	T entity = null;
    	
    	try {
    		entityManager = Factory.getFactory().createEntityManager();
    		TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    		typedQuery.setParameter("value", value);
    		entity = typedQuery.getSingleResult();
    		
    	} catch (NoResultException exception) {
    		entity = null;
    		
    	} catch(Exception e) {
    		System.err.println("Exception while selecting unique: " +field +" with the value: " +value +" from the class: " +clazz);
    		e.printStackTrace();
    		
    	} finally {
    		if (entityManager != null) {
    			entityManager.close();
    		}
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected unique: " +entity);
		}
    	
    	return entity;
	}
	
	/**
	 * Selects the unique entity, in the specified table, which
	 * has the given fields (names in the Java class) set to the given values
	 * @return the unique entity that satisfies the requirements, or
	 * <code>null</code> if it doesn't exist or an exception occurred
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 */
	public static <T> T selectUnique (Class <T> clazz, String [] fields, Object ... values) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(fields);
		Objects.requireNonNull(values);
		
		if (DEBUG_MODE) {
    		System.out.println("Selecting unique: " +fields +" with the values: " +values +" from the class: " +clazz);
		}
		
    	String query = "SELECT c FROM "+clazz.getName()+" c WHERE ";
    	for (int i = 0; i < fields.length; i++) {
    		if (i == fields.length -1) {
    			query += "c." +fields[i] +" = :value_" +i;
    		} else {
    			query += "c." +fields[i] +" = :value_" +i +" AND ";
    		}
    	}
    	
    	EntityManager entityManager = null;
    	T entity = null;
    	
    	try {
	    	entityManager = Factory.getFactory().createEntityManager();
	    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
	    	for (int i = 0; i < values.length; i++) {
	    		typedQuery.setParameter("value_" +i, values[i]);
	    	}
    		entity = typedQuery.getSingleResult();
    		
    	} catch (NoResultException exception) {
    		entity = null;
    		
    	} catch(Exception e) {
    		System.err.println("Exception while selecting unique: " +fields +" with the values: "+values+" from the class: " +clazz);
    		e.printStackTrace();
    		
    	} finally {
    		if (entityManager != null) {
    			entityManager.close();
    		}
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected unique: " +entity);
		}
    	
    	return entity;
	}
	
	/**
	 * Selects the entities, in the specified table, which
	 * have the given field (name in the Java class) set to the given value
	 * @return a {@link List} of the entities that satisfies the requirements, or
	 * <code>null</code> if an exception occurred
	 * @throws NullPointerException if clazz or field is <code>null</code>
	 */
	public static <T> List <T> selectMultiple (Class <T> clazz, String field, Object value) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(field);
		
		if (DEBUG_MODE) {
			System.out.println("Selecting multiple: " +field +" with the value: " +value +" from the class: " +clazz);
		}
		
		String query = "SELECT c FROM "+clazz.getName()+" c WHERE c."+field+" = :value";
		
		EntityManager entityManager = null;
		List <T> list = null;

    	try {
    		entityManager = Factory.getFactory().createEntityManager();
    		TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    		typedQuery.setParameter("value", value);
    		list = typedQuery.getResultList();
    		
    	} catch(Exception e) {
    		System.err.println("Exception while selecting multiple: " +field +" with the value: " +value +" from the class: " +clazz);
    		e.printStackTrace();
    		
    	} finally {
    		if (entityManager != null) {
    			entityManager.close();
    		}
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected multiple: " + list);
		}
    	
    	return list;
	}
	
	/**
	 * Selects the entities, in the specified table, which
	 * have the given fields (names in the Java class) set to the given values
	 * @return a {@link List} of the entities that satisfies the requirements, or
	 * <code>null</code> if an exception occurred
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 */
	public static <T> List <T> selectMultiple (Class <T> clazz, String [] fields, Object ... values) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(fields);
		Objects.requireNonNull(values);
		
		if (DEBUG_MODE) {
    		System.out.println("Selecting multiple: " +fields +" with the values: " +values +" from the class: " +clazz);
		}
		
		String query = "SELECT c FROM "+clazz.getName()+" c WHERE ";
    	for (int i = 0; i < fields.length; i++) {
    		if (i == fields.length -1) {
    			query += "c." +fields[i] +" = :value_" +i;
    		} else {
    			query += "c." +fields[i] +" = :value_" +i +" AND ";
    		}
    	}
    	
    	EntityManager entityManager = null;
    	List <T> list = null;
    	
    	try {
	    	entityManager = Factory.getFactory().createEntityManager();
	    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
	    	for (int i = 0; i < values.length; i++) {
	    		typedQuery.setParameter("value_" +i, values[i]);
	    	}
    		list = typedQuery.getResultList();
    		
    	} catch(Exception e) {
    		System.err.println("Exception while selecting multiple: " +fields +" with the values: " +values +" from the class: " +clazz);
    		e.printStackTrace();
    	} finally {
    		if (entityManager != null) {
    			entityManager.close();
    		}
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected multiple: " + list);
		}
    	
    	return list;
	}
	
	/**
	 * <p>Queries the DB with the given native query
	 * <p>Note that the query should be in the
	 * underlying DBs' natives' language. And 
	 * should identify the column and tables as they
	 * are in the DB, and not as they are here in the Java classes. 
	 * In other words, the query should be the same as if
	 * it was executed directly in the DB
	 * @return a {@link List} result of the native given query,
	 * or <code>null</code> if an exception occurred
	 * @throws NullPointerException if any of the parameters are <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static <T> List <T> nativeQuery (Class <T> clazz, String query) {
		Objects.requireNonNull(clazz);
		Objects.requireNonNull(query);
		
		if (DEBUG_MODE) {
    		System.out.println("Querying: " +query +" from the class: " +clazz);
		}
		
		EntityManager entityManager = null;
		List <T> list = null;

    	try {
    		entityManager = Factory.getFactory().createEntityManager();
    		Query _query = entityManager.createNativeQuery(query, clazz);
    		list = _query.getResultList();
    		
    	} catch(Exception e) {
    		System.err.println("Exception while querying: " +query +" from the class: " +clazz);
    		e.printStackTrace();
    		
    	} finally {
    		if (entityManager != null) {
    			entityManager.close();
    		}
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Queried: " + list);
		}
    	
    	return list;
	}
	
	/**
	 * An {@link Enum} to specify which
	 * operation to perform in
	 * {@link Manager#performOperation(Operation, Object, Class, Object)}
	 */
	private static enum Operation {
		PERSIST,
		MERGE,
		REMOVE
	}
	
}
