package KTGK.de03.service;

import KTGK.de03.dao.*;
import KTGK.de03.dao.impl.UserDaoImpl_24133056;
import KTGK.de03.entity.User_24133056;
import java.util.*;

public class AuthService_24133056 {
	private final UserDao_24133056 d = new UserDaoImpl_24133056();

	public Optional<User_24133056> login(String n, String p) {
		return d.find(n).filter(User_24133056::isActive).filter(u -> u.getPassword().equals(PasswordUtil_24133056.hash(p)));
	}

	public void register(User_24133056 u) {
		if (d.find(u.getUsername()).isPresent())
			throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
		if (d.findByEmail(u.getEmail()).isPresent())
			throw new IllegalArgumentException("Email đã được sử dụng");
		u.setPassword(PasswordUtil_24133056.hash(u.getPassword()));
		u.setActive(false);
		d.save(u);
	}

	public void activate(String n) {
		User_24133056 u = d.find(n).orElseThrow();
		u.setActive(true);
		d.save(u);
	}
}



