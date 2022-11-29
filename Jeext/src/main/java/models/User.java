package models;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import models.core.Model;
import models.core.Permission;
import util.Strings;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.FetchType;

//TODO: remove non nullable form user db

@Entity
@Table (name= "user")
public class User extends Model <User> implements Serializable {
	private static final long serialVersionUID = 1L;
      
	public static void main(String[] args) {
		System.out.println(new User().clazz.find(1));
	}
	
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

	@Column(nullable= false)
	private String first_name;

	@Column(nullable= false)
	private String last_name;

	@Column(nullable= false)
	private Boolean isMale; // https://www.youtube.com/watch?v=QJJYpsA5tv8

	@Column
	private LocalDate creation_date;

	@ElementCollection(fetch= FetchType.EAGER)
	@Column(name= "name")
	@CollectionTable(name= "permission")
	@Enumerated(EnumType.STRING)
	private Set <Permission> permissions;

	public String getFormalFullName () {
		return (isMale) ? "Mr. " +getFullName(): "Ms. " +getFullName();
	}
	
	public String getFullName () {
		return getLast_name() +" " +getFirst_name();
	}

	@Override
	public long getId() {
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