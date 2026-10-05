package KTGK.de03.dao;

import jakarta.persistence.*;

public final class JpaUtil_24133056 {
	private static final EntityManagerFactory F = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");

	private JpaUtil_24133056() {
	}

	public static EntityManager em() {
		return F.createEntityManager();
	}

	public static void close() {
		if (F.isOpen())
			F.close();
	}
}



