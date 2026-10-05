package KTGK.de03.dao.impl;

import jakarta.persistence.*;
import KTGK.de03.dao.*;
import KTGK.de03.entity.Video_24133056;
import java.util.*;

public class VideoDaoImpl_24133056 implements VideoDao_24133056 {
	public Optional<Video_24133056> find(String id) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return Optional.ofNullable(e.find(Video_24133056.class, id));
		}
	}

	public List<Video_24133056> page(int p, int s) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("from Video_24133056 v order by v.videoId", Video_24133056.class).setFirstResult((p - 1) * s)
					.setMaxResults(s).getResultList();
		}
	}

	public List<Video_24133056> pageByCategory(int id, int p, int s) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("from Video_24133056 v where v.active=true and v.category.categoryId=:id order by v.videoId",
					Video_24133056.class).setParameter("id", id).setFirstResult((p - 1) * s).setMaxResults(s).getResultList();
		}
	}

	public long count() {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("select count(v) from Video_24133056 v", Long.class).getSingleResult();
		}
	}

	public long countByCategory(int id) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("select count(v) from Video_24133056 v where v.active=true and v.category.categoryId=:id",
					Long.class).setParameter("id", id).getSingleResult();
		}
	}

	public void save(Video_24133056 v) {
		EntityManager e = JpaUtil_24133056.em();
		EntityTransaction t = e.getTransaction();
		try {
			t.begin();
			e.merge(v);
			t.commit();
		} catch (RuntimeException x) {
			if (t.isActive())
				t.rollback();
			throw x;
		} finally {
			e.close();
		}
	}

	public void delete(String id) {
		EntityManager e = JpaUtil_24133056.em();
		EntityTransaction t = e.getTransaction();
		try {
			t.begin();
			Video_24133056 v = e.find(Video_24133056.class, id);
			if (v != null)
				e.remove(v);
			t.commit();
		} catch (RuntimeException x) {
			if (t.isActive())
				t.rollback();
			throw x;
		} finally {
			e.close();
		}
	}

	public long likes(String id) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("select count(f) from Favorite_24133056 f where f.video.videoId=:id", Long.class)
					.setParameter("id", id).getSingleResult();
		}
	}

	public long shares(String id) {
		try (EntityManager e = JpaUtil_24133056.em()) {
			return e.createQuery("select count(s) from Share_24133056 s where s.video.videoId=:id", Long.class)
					.setParameter("id", id).getSingleResult();
		}
	}
}



