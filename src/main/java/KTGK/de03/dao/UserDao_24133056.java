package KTGK.de03.dao;

import KTGK.de03.entity.User_24133056;
import java.util.Optional;

public interface UserDao_24133056 {
	Optional<User_24133056> find(String id);

	Optional<User_24133056> findByEmail(String email);

	void save(User_24133056 u);
}



