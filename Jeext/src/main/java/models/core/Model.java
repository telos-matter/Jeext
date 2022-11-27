package models.core;

import java.util.List;

import dao.Manager;

public abstract class Model <T extends Model <T>> {
	
	public ClassAccessor clazz;
	
	public Model () {
		clazz = new ClassAccessor();
	}
	
	public abstract long getId ();
	
	public boolean equalsId (T entity) {
		return (entity == null) ? false : this.getId() == entity.getId();
	}
	
	public void insert () {
		Manager.insert(this);
	}
	
	public void update () {
		Manager.update(this);
	}
	
	public void delete () {
		Manager.delete(this.clazz(), this.getId());
	}
	
	@SuppressWarnings("unchecked")
	private Class <T> clazz () {
		return (Class <T>) this.getClass();
	}
	
	public class ClassAccessor {
		
		public List <T> all () {
			return Manager.selectAll(clazz());
		}
		
		public T find (long id) {
			return Manager.find(clazz(), id);
		}
		
		public T unique (String field, Object value) {
			return Manager.selectUnique(clazz(), field, value);
		}
		
		public T unique (String [] fields, Object ... values) {
			return Manager.selectUnique(clazz(), fields, values);
		}
		
		public List <T> multiple (String field, Object value) {
			return Manager.selectMultiple(clazz(), field, value);
		}
		
		public List <T> multiple (String [] fields, Object ... values) {
			return Manager.selectMultiple(clazz(), fields, values);
		}
			
		public List <T> query (String query) {
			return Manager.query(clazz(), query);
		}
		
	}
}
