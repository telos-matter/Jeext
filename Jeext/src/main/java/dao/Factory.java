package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A static class from which the user can retrieve an {@link EntityManager}
 * from the single {@link EntityManagerFactory} 
 */
public class Factory {
	
	/**
	 * <p>Change the persistence unit name ("PERSISTENCE_UNIT") accordingly. Should be
	 * the same as the one in the persistence.xml file
	 * <p>This persistence unit represents the DB you will be working with
	 */
	private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("PERSISTENCE_UNIT");

	public static EntityManagerFactory getFactory () {
		return FACTORY;
	}
	
	public static EntityManager createEntityManager () {
		return getFactory().createEntityManager();
	}
	
	public static void closeFactory () {
		FACTORY.close(); // Then Centurion said: "And fire the employees too, Sir?"
	}
	
}
