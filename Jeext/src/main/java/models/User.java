package models;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import util.Strings;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

@Entity
@Table (name= "user")
public class User extends Model <User> implements Serializable {
	private static final long serialVersionUID = 1L;
      
	public User () {
//		this.permissions = new HashSet <> ();
//		this.isActive = true;
		this.creation_date = LocalDate.now();
	}
      
	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;

	@Column
	private String username;

	@Column
	private String password;

	@Column
	private String email;

	@Column
	private String first_name;

	@Column
	private String last_name;

	@Column
	private Boolean isMale; // https://www.youtube.com/watch?v=QJJYpsA5tv8

	@Column
	@Temporal (TemporalType.DATE)
	private LocalDate creation_date;

// Role instead	
//	@ManyToMany(fetch= FetchType.EAGER)
//	@JoinTable (
//		name= "user_permission",
//		joinColumns= @JoinColumn (name= "user_id", referencedColumnName= "id"),
//		inverseJoinColumns= @JoinColumn (name= "permission_id", referencedColumnName= "id")
//		)
//	private Set <Permission> permissions;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email
				+ ", first_name=" + first_name + ", last_name=" + last_name + ", isMale=" + isMale + ", creation_date="
				+ creation_date + "]";
	}
	
}