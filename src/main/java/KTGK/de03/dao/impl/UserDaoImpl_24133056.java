package KTGK.de03.dao.impl;

import jakarta.persistence.*;
import KTGK.de03.dao.*;
import KTGK.de03.entity.User_24133056;
import java.util.*;

public class UserDaoImpl_24133056 implements UserDao_24133056 {
	public Optional<User_24133056> find(String id) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return Optional.ofNullable(e.find(User_24133056.class, id));
		}
	}

	public Optional<User_24133056> findByEmail(String m) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("from User_24133056 u where lower(u.email)=lower(:m)", User_24133056.class).setParameter("m", m)
					.getResultStream().findFirst();
		}
	}

	public void save(User_24133056 u) {
		EntityManager e = JpaUtil_24133056.em();
		EntityTransaction t = e.getTransaction();
		try {
			t.begin();
			e.merge(u);
			t.commit();
		} catch (RuntimeException x) {
			if (t.isActive())
				t.rollback();
			throw x;
		} finally {
			e.close();
		}
	}
}



