package KTGK.de03.service;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.Test;
import KTGK.de03.entity.*;

class CommercePersistenceTest {
    @Test void realPersistenceUnitBootsWithBothVideoAndCommerceMappings() {
        // Keep the real persistence.xml mappings and schema action. Override only DB access.
        try (var factory = Persistence.createEntityManagerFactory("jpa-hibernate-mysql", Map.of(
                "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:full-app;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "jakarta.persistence.jdbc.user", "sa",
                "jakarta.persistence.jdbc.password", "",
                "hibernate.dialect", "org.hibernate.dialect.H2Dialect",
                "hibernate.show_sql", "false",
                "hibernate.format_sql", "false"))) {
            try (var em = factory.createEntityManager()) {
                assertEquals(0L, em.createQuery("select count(v) from Video_24133056 v", Long.class).getSingleResult());
                assertEquals(0L, em.createQuery("select count(o) from ShopOrder_24133056 o", Long.class).getSingleResult());
                em.getTransaction().begin();
                em.persist(new User_24133056("buyer", "test", "Buyer", "buyer@example.test", false, true));
                var product = new Product_24133056("PERSISTENCE", "Sản phẩm", new BigDecimal("1000"), 1);
                em.persist(product); em.getTransaction().commit();
                var service = new CommerceService_24133056(factory::createEntityManager, Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")));
                var cart = new Cart_24133056("buyer");
                service.changeCart("buyer", cart, 0, "add", product.getId(), 1);
                long order = service.checkout("buyer", cart, service.quote("buyer", cart),
                        new CommerceValidation_24133056.Shipping("Buyer", "0901234567", "Địa chỉ", ""), "COD");
                assertEquals(OrderStatus_24133056.NEW, service.order("buyer", order).getStatus());
            }
        }
    }
}
