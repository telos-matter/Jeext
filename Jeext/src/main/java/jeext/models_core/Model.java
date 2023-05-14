package jeext.models_core;

import java.util.List;
import java.util.UUID;

import jeext.dao.Manager;
import jeext.util.MCollections;
import models.User;

/**
 * <p>The base abstract class that all models in the application <i>should</i>
 * extend.
 * <p>It facilitates using the DAO on both the entities them selves
 * as well as the tables (which is synonym with Java {@link Class}es) they
 * belong to.
 * <p>The models should define the abstract method {@link #getId()} which allows
 * for the existence of the method {@link #equalsId(Model)}, upon which
 * all of {@link MCollections} methods are built.
 * <p>This class however comes with the inconvenience that it doesn't allow
 * direct inheritance between the models, as that each model should 
 * indicate its own class in the generic type. For example the {@link User}
 * class extends this {@link Model} class in the following
 * way: ".. class User extends Model &lt;User&gt; implements ..". However
 * this inconvenience can be overcome by simply having a reference to
 * the base class from the extending class, and/or vice versa. But it of course won't
 * allow for polymorphisme of methods and what not..
 * 
 * @author telos_matter
 */
public abstract class Model <T extends Model <T>> {
	
	/**
	 * <p>The accessor to the models' classes' (synonym with table)
	 * DAO functionalities
	 * <p>You of course need a non-<code>null</code> reference
	 * of the entity to be able to access them
	 */
	public ClassAccessor clazz;
	
	/**
	 * Constructor which is always called first by default
	 */
	public Model () {
		clazz = new ClassAccessor();
	}
	
	/**
	 * <p>The main abstract method that all {@link Model}s
	 * should define, it is mainly used in {@link #equalsId(Model)}
	 * <p>The actual class of the returned ID should be one
	 * that overrides the {@link Object#equals(Object)} method. If
	 * an unconventional ID type is used, then know that most
	 * Java IDE have functions that can auto-generate the method
	 * for you. Otherwise IDs such as {@link UUID} already have
	 * their {@link #equals(Object)} method redefined
	 * <p>Know that having primitives (such as int or long) as
	 * an ID would work just fine
	 * @return the unique identifier of this entity
	 * @see #equalsId(Model)
	 */
	public abstract Object getId ();
	
	/**
	 * <p>Tests for the equality of two entities by
	 * comparing their IDs.
	 * <p>In the same spirit as
	 * {@link Object#equals(Object)} but for {@link Model}s,
	 * since there can be multiple different references to
	 * the same entity
	 * @return <code>true</code> if the IDs of the two
	 * entities are equal, <code>false</code> if they are
	 * not or if the other entity is <code>null</code>
	 * @see #getId()
	 */
	public boolean equalsId (T entity) {
		return (entity == null) ? false : this.getId().equals(entity.getId());
	}
	
	/**
	 * Calls upon {@link Manager#insert(Object)}
	 */
	public void insert () {
		Manager.insert(this);
	}
	
	/**
	 * Calls upon {@link Manager#update(Object)}
	 */
	public void update () {
		Manager.update(this);
	}
	
	/**
	 * Calls upon {@link Manager#delete(Class, Object)}
	 */
	public void delete () {
		Manager.delete(this.clazz(), this.getId());
	}
	
	/**
	 * To easily retrieve the actual {@link Class}
	 * of this {@link Model}
	 */
	@SuppressWarnings("unchecked")
	private Class <T> clazz () {
		return (Class <T>) this.getClass();
	}
	
	/**
	 * Facilitates class/table level DAO methods
	 */
	public class ClassAccessor {
		
		/**
		 * Calls upon {@link Manager#selectAll(Class)}
		 */
		public List <T> all () {
			return Manager.selectAll(clazz());
		}
		
		/**
		 * Calls upon {@link Manager#find(Class, Object)}
		 */
		public T find (Object id) {
			return Manager.find(clazz(), id);
		}
		
		/**
		 * Calls upon {@link Manager#selectUnique(Class, String, Object)}
		 */
		public T unique (String field, Object value) {
			return Manager.selectUnique(clazz(), field, value);
		}
		
		/**
		 * Calls upon {@link Manager#selectUnique(Class, String[], Object...)}
		 */
		public T unique (String [] fields, Object ... values) {
			return Manager.selectUnique(clazz(), fields, values);
		}
		
		/**
		 * Calls upon {@link Manager#selectMultiple(Class, String, Object)}
		 */
		public List <T> multiple (String field, Object value) {
			return Manager.selectMultiple(clazz(), field, value);
		}
		
		/**
		 * Calls upon {@link Manager#selectMultiple(Class, String[], Object...)}
		 */
		public List <T> multiple (String [] fields, Object ... values) {
			return Manager.selectMultiple(clazz(), fields, values);
		}
		
		/**
		 * Calls upon {@link Manager#nativeQuery(Class, String)}
		 */
		public List <T> nativeQuery (String query) {
			return Manager.nativeQuery(clazz(), query);
		}
		
	}

	/**
	 * <p>It is a good idea, or at least when debugging, to
	 * override the {@link Object#toString()} method in
	 * every {@link Model}, as to get more detailed information
	 * from the DAO functions
	 * <p>Know that most Java IDEs have a functions that
	 * can auto-generate this method for you
	 * @see Manager
	 */
	@Override
	public String toString() {
		return "Model (" + this.clazz()+ ") [ID: " +this.getId() +"]";
	}
	
}
