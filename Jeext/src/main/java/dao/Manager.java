package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;


public class Manager {

	private static boolean DEBUG_MODE = true;
	
	public static void insert (Object entity) {
		if (DEBUG_MODE) {
			System.out.println("Inserting: " + entity);
		}
		
		EntityManager entityManager = Factory.createEntityManager();
        EntityTransaction entityTransaction = null;
        
        try {
        	entityTransaction = entityManager.getTransaction();
        	entityTransaction.begin();
            entityManager.persist(entity);
            entityTransaction.commit();
        } catch (Exception exception) {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            System.out.println("Error occured while trying to insert: " +entity);
            exception.printStackTrace();
        } finally {
            entityManager.close();
        }
        
        if (DEBUG_MODE) {
        	System.out.println("After inserting: " + entity);
        }
	}
	
	public static void update (Object entity) {
		if (DEBUG_MODE) {
			System.out.println("Updating: " + entity);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();
        EntityTransaction entityTransaction = null;
        
        try {
        	entityTransaction = entityManager.getTransaction();
        	entityTransaction.begin();
            entityManager.merge(entity);
            entityTransaction.commit();
        } catch (Exception exception) {
            if (entityTransaction != null) {
                entityTransaction.rollback();
            }
            System.out.println("Error occured while trying to update: " +entity);
            exception.printStackTrace();
        } finally {
            entityManager.close();
        }
        
		if (DEBUG_MODE) {
			System.out.println("After updating: " + entity);
		}
	}
	
	// TODO: remove boolean return, make sure there is a proper way to the check, or mention it in doc
	public static boolean delete (Class <?> clazz, Object key) {
		if (DEBUG_MODE) {
			System.out.println("Deleting: " + key +" for the class: " +clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();
		EntityTransaction entityTransaction = null;
		try {
			entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			entityManager.remove(entityManager.find(clazz, key));
			entityTransaction.commit();

			if (entityManager.find(clazz, key) == null) {
				if (DEBUG_MODE) {
					System.out.println("Deleted succesfully.");
				}
				
				return true;
			} else {
				if (DEBUG_MODE) {
					System.out.println("/!\\ " +key +" for the class: " +clazz +"was NOT deleted.");
				}
				
				return false;
			}
		} catch (Exception exception) {
			if (entityTransaction != null) {
				entityTransaction.rollback();
			}
			System.out.println("Error occured while trying to delete: "  + key +" for the class: " +clazz);
			exception.printStackTrace();
			
			return false;
		} finally {
			entityManager.close();
		}
	}
	
	public static <T> List <T> selectAll (Class <T> clazz) {
		if (DEBUG_MODE) {
    		System.out.println("Selecting all from the class: " + clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();

    	String query = "SELECT c FROM "+clazz.getName()+" c";
    	
    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    	
    	List <T> list = null;
    	try {
    		list = typedQuery.getResultList();
    	} catch(Exception exception) {
    		System.out.println("Error occured while trying to select all from the class: " +clazz);
    		exception.printStackTrace();
    	} finally {
    		entityManager.close();
    	}
    	
    	if (DEBUG_MODE) {
			System.out.println("Selected all: " + list);
		}
    	
    	return list;
	}
	
	public static <T> T find (Class <T> clazz, long key) {
		if (DEBUG_MODE) {
			System.out.println("Trying to find: " +key +" for the class: " +clazz);
		}
		
        EntityManager entityManager = Factory.getFactory().createEntityManager();
        
        T entity = null;
        try {
        	entity = entityManager.find(clazz, key);        	
        } catch (Exception exception) {
        	System.out.println("Error occured while trying to find: " +key +" for the class: " +clazz);
            exception.printStackTrace();
        } finally {
        	entityManager.close();
        }
        
		if (DEBUG_MODE) {
			System.out.println("Found: " + entity);
		}
		
		return entity;
	}
	
	public static <T> T selectUnique (Class <T> clazz, String field, Object value) {
		if (DEBUG_MODE) {
			System.out.println("Selecting unique: "  +field +" with the value: " +value +" for the class: " +clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();

    	String query = "SELECT c FROM " +clazz.getName() +" c WHERE c." +field +" = :value";
    	
    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    	typedQuery.setParameter("value", value);
    	
    	T entity = null;
    	try {
    		entity = typedQuery.getSingleResult();
    	} catch (NoResultException exception) {
    		entity = null;
    	} catch(Exception exception) {
    		System.out.println("Error occured while trying to select unique: " +field +" with the value: " +value +" for the class: " +clazz);
    		exception.printStackTrace();
    	} finally {
    		entityManager.close();
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected unique: " +entity);
		}
    	
    	return entity;
	}
	
	public static <T> T selectUnique (Class <T> clazz, String [] fields, Object ... values) {
		if (DEBUG_MODE) {
    		System.out.println("Selecting unique: " +fields +" with the values: " +values +" for the class: " +clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();

    	String query = "SELECT c FROM "+clazz.getName()+" c WHERE ";
    	for (int i = 0; i < fields.length; i++) {
    		if (i == fields.length -1) {
    			query += "c." +fields[i] +" = :value_" +i;
    		} else {
    			query += "c." +fields[i] +" = :value_" +i +" AND ";
    		}
    	}
    	
    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    	for (int i = 0; i < values.length; i++) {
    		typedQuery.setParameter("value_" +i, values [i]);
    	}
    	
    	T entity = null;
    	try {
    		entity = typedQuery.getSingleResult();
    	} catch (NoResultException exception) {
    		entity = null;
    	} catch(Exception exception) {
    		System.out.println("Error occured while trying to select unique: " +fields +" with the values: "+values+" for the class: " +clazz);
    		exception.printStackTrace();
    	} finally {
    		entityManager.close();
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected unique: " +entity);
		}
    	
    	return entity;
	}
	
	public static <T> List <T> selectMultiple (Class <T> clazz, String field, Object value) {
		if (DEBUG_MODE) {
			System.out.println("Selecting multiple: " +field +" with the value: " +value +" for the class: " +clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();

    	String query = "SELECT c FROM "+clazz.getName()+" c WHERE c."+field+" = :value";
    	
    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    	typedQuery.setParameter("value", value);
    	
    	List <T> list = null;
    	try {
    		list = typedQuery.getResultList();
    	} catch(Exception exception) {
    		System.out.println("Error occured while trying to select multiple: " +field +" with the value: " +value +" for the class: " +clazz);
    		exception.printStackTrace();
    	} finally {
    		entityManager.close();
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected multiple: " + list);
		}
    	
    	return list;
	}
	
	public static <T> List <T> selectMultiple (Class <T> clazz, String [] fields, Object ... values) {
		if (DEBUG_MODE) {
    		System.out.println("Selecting multiple: " +fields +" with the values: " +values +" for the class: " +clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();

		String query = "SELECT c FROM "+clazz.getName()+" c WHERE ";
    	for (int i = 0; i < fields.length; i++) {
    		if (i == fields.length -1) {
    			query += "c." +fields[i] +" = :value_" +i;
    		} else {
    			query += "c." +fields[i] +" = :value_" +i +" AND ";
    		}
    	}
    	
    	TypedQuery <T> typedQuery = entityManager.createQuery(query, clazz);
    	for (int i = 0; i < values.length; i++) {
    		typedQuery.setParameter("value_" +i, values [i]);
    	}
    	
    	List <T> list = null;
    	try {
    		list = typedQuery.getResultList();
    	} catch(Exception exception) {
    		System.out.println("Error occured while trying to select multiple: " +fields +" with the values: " +values +" for the class: " +clazz);
    		exception.printStackTrace();
    	} finally {
    		entityManager.close();
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected multiple: " + list);
		}
    	
    	return list;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List <T> query (Class <T> clazz, String query) {
		if (DEBUG_MODE) {
    		System.out.println("Selecting multiple: " +query +" for the class: " +clazz);
		}
		
		EntityManager entityManager = Factory.getFactory().createEntityManager();

		Query _query = entityManager.createNativeQuery(query, clazz);
    	
    	List <T> list = null;
    	try {
    		list = _query.getResultList();
    	} catch(Exception exception) {
    		System.out.println("Error occured while trying to select multiple: " +query +" for the class: " +clazz);
    		exception.printStackTrace();
    	} finally {
    		entityManager.close();
    	}
    	
    	if (DEBUG_MODE) {
    		System.out.println("Selected multiple: " + list);
		}
    	
    	return list;
	}
	
}
