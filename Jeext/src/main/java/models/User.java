package models;

import java.io.Externalizable;
import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import jeext.controller.Filter;
import jeext.models_core.Model;
import jeext.models_core.Permission;
import jeext.util.Strings;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.FetchType;

/**
 * <p>An example class of a {@link Model}, and also
 * a very common/needed class/table in most web application
 * <p>This class is used in the {@link Filter} and thus
 * it needs to stay even if it's not used (unless if you
 * are going to remove all references and places where
 * it's used/mentioned, the other components would still work
 * fine without it if you do so).
 * But keep in mind that, in case your web application doesn't need
 * a user class or does not even use a DB, you can keep this class
 * and the DAO part of Jeext and they won't be called/used if you don't
 * use them
 * <p>Add, remove and adjust this class to fit your needs, the
 * <strong>only</strong> thing
 * that needs to stay is the {@link #permissions} member, and its
 * {@link #getPermissions()} method
 * <p>Do remember however, in case you are using this class with your DB (
 * or any other class that you are going to use with your DB), that
 * it needs to be a Bean, i.e. it should:
 * <ul>
 * <li>Implement either {@link Serializable} or {@link Externalizable}
 * <li>Have a no-arg constructor
 * <li>Have public setter and getter methods for all its members
 * <li>Make all its members private
 * </ul>
 */
@Entity
@Table (name= "user")
public class User extends Model <User> implements Serializable {
	private static final long serialVersionUID = 1L;
      
	/**
	 * It is a good idea to have default values
	 * be in your {@link Model}s constructor, that way
	 * you don't have to worry about them later on
	 */
	public User () {
		this.permissions = new HashSet <> ();
		this.creation_date = LocalDate.now();
	}
      
	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;

	@Column(nullable= false, unique= true)
	private String username;

	@Column(nullable= false)
	private String password;

	@Column
	private String email;

	@Column()
	private String first_name;

	@Column()
	private String last_name;

	@Column()
	private Boolean isMale; // https://www.youtube.com/watch?v=QJJYpsA5tv8

	@Column
	private LocalDate creation_date;

	/**
	 * <p>{@link Permission}s that this
	 * user has
	 * <p>This member needs to stay
	 */
	@ElementCollection(fetch= FetchType.EAGER)
	@Column(name= "name")
	@CollectionTable(name= "permission")
	@Enumerated(EnumType.STRING)
	private Set <Permission> permissions;

	/**
	 * A functional method that returns
	 * the formal name of this {@link User}
	 */
	public String getFormalFullName () {
		if (isMale != null) {
			return (isMale) ? "Mr. " +getFullName(): "Ms. " +getFullName();
		} else {
			return getFullName();
		}
	}
	
	/**
	 * A functional method that returns
	 * the full name of this {@link User}
	 */
	public String getFullName () {
		return getLast_name() +" " +getFirst_name();
	}

	@Override
	public Object getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = Strings.forceCapitalize(first_name);
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = Strings.toUpperCase(last_name);
	}

	public Boolean getIsMale() {
		return isMale;
	}

	public void setIsMale(Boolean isMale) {
		this.isMale = isMale;
	}

	public LocalDate getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(LocalDate creation_date) {
		this.creation_date = creation_date;
	}
	
	/**
	 * This method needs to stay
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", first_name=" + first_name + ", last_name=" + last_name + ", isMale=" + isMale + ", creation_date="
				+ creation_date + ", permissions=" + permissions + "]";
	}

}