package KTGK.de03.dao.impl;

import jakarta.persistence.*;
import KTGK.de03.dao.*;
import KTGK.de03.entity.Category_24133056;
import java.util.*;

public class CategoryDaoImpl_24133056 implements CategoryDao_24133056 {
	public List<Category_24133056> findAll() {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("from Category_24133056 c where c.status=true order by c.categoryName", Category_24133056.class)
					.getResultList();
		}
	}

	public Optional<Category_24133056> find(int id) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return Optional.ofNullable(e.find(Category_24133056.class, id));
		}
	}

	public void save(Category_24133056 c) {
		EntityManager e = JpaUtil_24133056.em();
		EntityTransaction t = e.getTransaction();
		try {
			t.begin();
			e.persist(c);
			t.commit();
		} finally {
			if (t.isActive())
				t.rollback();
			e.close();
		}
	}
}



