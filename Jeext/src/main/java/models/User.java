package models;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;


@Entity
@Table (name= "user")
public class User extends Model <User> implements Serializable {
	private static final long serialVersionUID = 1L;
      
	public User () {
		
	}
      
	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;

	@Column
	private String username;

	@Column
	private String password;

	@Override
	public long getId() {
		return id;
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

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + "]";
	}

}