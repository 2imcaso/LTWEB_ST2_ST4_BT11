package KTGK.de03.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User_24133056 {
	@Id
	@Column(length = 50)
	private String username;
	@Column(length = 50)
	private String password;
	@Column(length = 15)
	private String phone;
	@Column(length = 50)
	private String fullname;
	@Column(length = 150, unique = true)
	private String email;
	private boolean admin;
	private boolean active;
	@Column(length = 500)
	private String images;

	public User_24133056() {
	}

	public User_24133056(String u, String p, String f, String e, boolean a, boolean x) {
		username = u;
		password = p;
		fullname = f;
		email = e;
		admin = a;
		active = x;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String v) {
		username = v;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String v) {
		password = v;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String v) {
		phone = v;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String v) {
		fullname = v;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String v) {
		email = v;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean v) {
		admin = v;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean v) {
		active = v;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String v) {
		images = v;
	}
}



