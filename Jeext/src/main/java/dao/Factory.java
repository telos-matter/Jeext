package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Factory {
	
	private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("PERSISTENCE_UNIT");

	public static EntityManagerFactory getFactory () {
		return FACTORY;
	}
	
	public static EntityManager createEntityManager () {
		return getFactory().createEntityManager();
	}
	
	public static void closeFactory () {
		FACTORY.close(); // Then Centurion said: And fire the employees too, Sir?
	}
	
}
