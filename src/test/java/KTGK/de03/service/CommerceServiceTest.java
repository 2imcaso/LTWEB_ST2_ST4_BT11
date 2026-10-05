package KTGK.de03.service;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import KTGK.de03.entity.*;
import KTGK.de03.service.CommerceValidation_24133056.Shipping;
import KTGK.de03.service.CommerceView_24133056.Quote;

class CommerceServiceTest {
    private static EntityManagerFactory factory;
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-05T16:59:59Z"), ZoneId.of("Asia/Ho_Chi_Minh"));
    private CommerceService_24133056 service;
    private long productId;
    private long otherId;

    @BeforeAll static void database() {
        factory = new Configuration()
                .addAnnotatedClass(User_24133056.class)
                .addAnnotatedClass(Category_24133056.class)
                .addAnnotatedClass(Video_24133056.class)
                .addAnnotatedClass(Product_24133056.class)
                .addAnnotatedClass(ShopOrder_24133056.class)
                .addAnnotatedClass(OrderItem_24133056.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:commerce;MODE=MySQL;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false")
                .buildSessionFactory();
    }
    @AfterAll static void closeDatabase() { if (factory != null) factory.close(); }
    @BeforeEach void seed() {
        service = new CommerceService_24133056(factory::createEntityManager, CLOCK);
        tx(em -> {
            em.createQuery("delete from OrderItem_24133056").executeUpdate();
            em.createQuery("delete from ShopOrder_24133056").executeUpdate();
            em.createQuery("delete from Product_24133056").executeUpdate();
            em.createQuery("delete from User_24133056").executeUpdate();
            em.persist(new User_24133056("alice", "test", "Alice", "alice@example.test", false, true));
            em.persist(new User_24133056("bob", "test", "Bob", "bob@example.test", false, true));
            Product_24133056 p = new Product_24133056("BOOK", "Sách", new BigDecimal("120000"), 5);
            Product_24133056 other = new Product_24133056("PEN", "Bút", new BigDecimal("15000"), 3);
            em.persist(p); em.persist(other); em.flush();
            productId = p.getId(); otherId = other.getId();
        });
    }

    @Test void addMergeUpdateRemoveAndClear() {
        var cart = cart("alice", productId, 1);
        service.changeCart("alice", cart, 1, "add", productId, 2);
        assertEquals(3, cart.getItems().get(productId));
        service.changeCart("alice", cart, 2, "update", productId, 5);
        assertEquals(new BigDecimal("600000"), service.cart("alice", cart).getTotal());
        service.changeCart("alice", cart, 3, "remove", productId, 0);
        assertTrue(cart.getItems().isEmpty());
        service.changeCart("alice", cart, 4, "add", otherId, 1);
        service.changeCart("alice", cart, 5, "clear", 0, 0);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test void quantityLimitAndOverflowLeaveCartUnchanged() {
        var cart = cart("alice", productId, 5);
        business(409, () -> service.changeCart("alice", cart, 1, "add", productId, 1));
        business(409, () -> service.changeCart("alice", cart, 1, "add", productId, Integer.MAX_VALUE));
        business(400, () -> service.changeCart("alice", cart, 1, "update", productId, 0));
        business(400, () -> service.changeCart("alice", cart, 1, "update", productId, -1));
        assertEquals(5, cart.getItems().get(productId));
        assertEquals(1, cart.getRevision());
    }

    @Test void staleRevisionAndUnknownActionsCannotMutateCart() {
        var cart = cart("alice", productId, 2);
        business(409, () -> service.changeCart("alice", cart, 0, "remove", productId, 0));
        business(400, () -> service.changeCart("alice", cart, 1, "anything", productId, 1));
        business(404, () -> service.changeCart("alice", cart, 1, "update", otherId, 1));
        business(404, () -> service.changeCart("alice", cart, 1, "remove", otherId, 0));
        assertEquals(Map.of(productId, 2), cart.getItems());
    }

    @Test void noCrossAccountCartAccessOrCheckout() {
        var cart = cart("alice", productId, 1);
        var quote = service.quote("alice", cart);
        business(403, () -> service.cart("bob", cart));
        business(403, () -> service.changeCart("bob", cart, 1, "clear", 0, 0));
        business(409, () -> service.checkout("bob", cart, quote, shipping(), "COD"));
        assertEquals(0, countOrders());
    }

    @Test void disabledAccountCannotUseSavedSessionToBuy() {
        var cart = cart("alice", productId, 1);
        var quote = service.quote("alice", cart);
        sql("update Users set active=false where username='alice'");
        business(403, () -> service.checkout("alice", cart, quote, shipping(), "COD"));
        business(403, () -> service.orders("alice", null, 1));
        business(403, () -> service.changeCart("alice", cart, 1, "clear", 0, 0));
        assertEquals(5, stock(productId));
    }

    @Test void emptyCartCannotCheckout() {
        business(409, () -> service.quote("alice", new Cart_24133056("alice")));
    }

    @Test void codCreatesSnapshotAndDecrementsStockThenClearsCart() {
        var cart = cart("alice", productId, 2);
        service.changeCart("alice", cart, 1, "add", otherId, 1);
        long id = service.checkout("alice", cart, service.quote("alice", cart), shipping(), "COD");
        var order = service.order("alice", id);
        assertEquals(new BigDecimal("255000"), order.getTotal());
        assertEquals(OrderStatus_24133056.NEW, order.getStatus());
        assertEquals("COD", order.getPaymentMethod());
        assertEquals(LocalDateTime.of(2026, 10, 5, 23, 59, 59), order.getCreatedAt());
        assertEquals(2, order.getItems().size());
        assertEquals(3, stock(productId));
        assertEquals(2, stock(otherId));
        assertTrue(cart.getItems().isEmpty());
        sql("update shop_products set name='Tên mới', price=1 where id=" + productId);
        sql("delete from shop_products where id=" + otherId);
        var snapshot = service.order("alice", id);
        assertEquals(new BigDecimal("255000"), snapshot.getTotal());
        assertEquals("Sách", snapshot.getItems().getFirst().getProductName());
        assertEquals(new BigDecimal("120000"), snapshot.getItems().getFirst().getUnitPrice());
        assertEquals(2, snapshot.getItems().size());
    }

    @Test void sameCheckoutTokenReturnsSameOrderWithoutConsumingNewCart() {
        var cart = cart("alice", productId, 1);
        var quote = service.quote("alice", cart);
        long first = service.checkout("alice", cart, quote, shipping(), "COD");
        service.changeCart("alice", cart, cart.getRevision(), "add", otherId, 1);
        assertEquals(first, service.checkout("alice", cart, quote, shipping(), "COD"));
        assertEquals(Map.of(otherId, 1), cart.getItems());
        assertEquals(first, service.completedCheckout("alice", quote.getToken()).orElseThrow());
        assertTrue(service.completedCheckout("bob", quote.getToken()).isEmpty());
        assertEquals(1, countOrders());
        assertEquals(4, stock(productId));
    }

    @Test void oldQuoteAndOtherPaymentCannotCreateOrder() {
        var cart = cart("alice", productId, 1);
        var quote = service.quote("alice", cart);
        business(400, () -> service.checkout("alice", cart, quote, shipping(), "CARD"));
        service.changeCart("alice", cart, 1, "update", productId, 2);
        business(409, () -> service.checkout("alice", cart, quote, shipping(), "COD"));
        assertEquals(0, countOrders());
        assertEquals(5, stock(productId));
    }

    @Test void priceChangeRequiresReviewAndFreshConfirmation() {
        var cart = cart("alice", productId, 1);
        var quote = service.quote("alice", cart);
        sql("update shop_products set price=130000 where id=" + productId);
        business(409, () -> service.checkout("alice", cart, quote, shipping(), "COD"));
        assertEquals(0, countOrders());
        assertEquals(1, cart.getItems().size());
        var fresh = service.quote("alice", cart);
        long id = service.checkout("alice", cart, fresh, shipping(), "COD");
        assertEquals(new BigDecimal("130000"), service.order("alice", id).getTotal());
    }

    @Test void laterLineFailureRollsBackPreviouslyReservedStock() {
        var cart = cart("alice", productId, 2);
        service.changeCart("alice", cart, 1, "add", otherId, 3);
        var quote = service.quote("alice", cart);
        sql("update shop_products set stock=1 where id=" + otherId);
        business(409, () -> service.checkout("alice", cart, quote, shipping(), "COD"));
        assertEquals(5, stock(productId));
        assertEquals(1, stock(otherId));
        assertEquals(0, countOrders());
        assertEquals(2, cart.getItems().size());
        assertFalse(service.cart("alice", cart).isCheckoutAllowed());
    }

    @Test void deletedOrInactiveProductsRemainRemovable() {
        var cart = cart("alice", productId, 1);
        sql("update shop_products set active=false where id=" + productId);
        assertFalse(service.cart("alice", cart).isCheckoutAllowed());
        business(409, () -> service.quote("alice", cart));
        sql("delete from shop_products where id=" + productId);
        assertFalse(service.cart("alice", cart).getLines().getFirst().isAvailable());
        service.changeCart("alice", cart, 1, "remove", productId, 0);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test void otherUsersOrdersAreNotVisible() {
        long id = place("alice");
        assertTrue(service.orders("bob", null, 1).getItems().isEmpty());
        business(404, () -> service.order("bob", id));
        business(404, () -> service.order("alice", Long.MAX_VALUE));
    }

    @ParameterizedTest @EnumSource(OrderStatus_24133056.class)
    void databaseStatusChangeIsReflectedInFilterAndDetail(OrderStatus_24133056 status) {
        long id = place("alice");
        sql("update shop_orders set status='" + status.name() + "', version=version+1 where id=" + id);
        assertEquals(status, service.order("alice", id).getStatus());
        assertEquals(1, service.orders("alice", status.name(), 1).getItems().size());
        var different = status == OrderStatus_24133056.NEW ? OrderStatus_24133056.CONFIRMED : OrderStatus_24133056.NEW;
        assertTrue(service.orders("alice", different.name(), 1).getItems().isEmpty());
        assertTrue(service.orders("bob", status.name(), 1).getItems().isEmpty());
    }

    @Test void invalidStatusAndPageRejectedAndLargeValidPageClamped() {
        business(400, () -> service.orders("alice", "PAID", 1));
        business(400, () -> service.orders("alice", "NEW' OR 1=1", 1));
        business(400, () -> service.orders("alice", null, 0));
        business(400, () -> service.orders("alice", null, Integer.MAX_VALUE));
        assertEquals(1, service.orders("alice", null, 100).getPage());
    }

    @Test void historyPaginationPreservesOwnershipAndStatus() {
        sql("update shop_products set stock=30 where id=" + productId);
        for (int i = 0; i < 12; i++) place("alice");
        place("bob");
        assertEquals(10, service.orders("alice", "NEW", 1).getItems().size());
        assertEquals(2, service.orders("alice", "NEW", 2).getItems().size());
        assertEquals(2, service.orders("alice", "NEW", 999).getPage());
        assertEquals(1, service.orders("bob", "NEW", 1).getItems().size());
    }

    @Test void competingCustomersCannotOversellLastStock() throws Exception {
        var a = cart("alice", productId, 4);
        var b = cart("bob", productId, 4);
        var qa = service.quote("alice", a);
        var qb = service.quote("bob", b);
        List<Long> outcomes = concurrent(() -> attempt("alice", a, qa), () -> attempt("bob", b, qb));
        assertEquals(1, outcomes.stream().filter(id -> id > 0).count());
        assertEquals(1, outcomes.stream().filter(id -> id == -409).count());
        assertEquals(1, stock(productId));
        assertEquals(1, countOrders());
    }

    @Test void concurrentSameTokenAcrossSessionCartsCreatesOneOrder() throws Exception {
        var a = cart("alice", productId, 2);
        var b = cart("alice", productId, 2);
        var quote = service.quote("alice", a);
        List<Long> outcomes = concurrent(() -> attempt("alice", a, quote), () -> attempt("alice", b, quote));
        assertTrue(outcomes.getFirst() > 0);
        assertEquals(outcomes.getFirst(), outcomes.getLast());
        assertEquals(1, countOrders());
        assertEquals(3, stock(productId));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"0", "-1", "1.5", "1e2", " 1", "+2", "2147483648", "999999999999999999999999", "abc"})
    void malformedQuantityRejected(String input) {
        business(400, () -> CommerceValidation_24133056.quantity(input));
    }

    @Test void shippingValidationRejectsBlankOversizedAndControlCharacters() {
        business(400, () -> new Shipping(" ", "0901234567", "Địa chỉ", ""));
        business(400, () -> new Shipping("A".repeat(101), "0901234567", "Địa chỉ", ""));
        business(400, () -> new Shipping("A", "abc", "Địa chỉ", ""));
        business(400, () -> new Shipping("A", "0901234567", " ", ""));
        business(400, () -> new Shipping("A", "0901234567", "X".repeat(501), ""));
        business(400, () -> new Shipping("A", "0901234567", "Địa chỉ", "X".repeat(1001)));
        business(400, () -> new Shipping("A\u0000", "0901234567", "Địa chỉ", ""));
        assertEquals("Nguyễn Văn A", new Shipping(" Nguyễn Văn A ", "+84901234567", " 12 Đường A ", null).recipient());
    }

    @Test void databaseRejectsUnknownStatusAndNegativeStock() {
        long id = place("alice");
        assertThrows(PersistenceException.class, () -> sql("update shop_orders set status='UNKNOWN' where id=" + id));
        assertThrows(PersistenceException.class, () -> sql("update shop_products set stock=-1 where id=" + productId));
        assertEquals(OrderStatus_24133056.NEW, service.order("alice", id).getStatus());
    }

    private Cart_24133056 cart(String user, long id, int quantity) {
        var cart = new Cart_24133056(user);
        service.changeCart(user, cart, 0, "add", id, quantity);
        return cart;
    }
    private long place(String user) {
        var cart = cart(user, productId, 1);
        return service.checkout(user, cart, service.quote(user, cart), shipping(), "COD");
    }
    private Shipping shipping() { return new Shipping("Nguyễn Văn A", "0901234567", "12 Đường A, TP Hồ Chí Minh", "Gọi trước khi giao"); }
    private long attempt(String user, Cart_24133056 cart, Quote quote) {
        try { return service.checkout(user, cart, quote, shipping(), "COD"); }
        catch (CommerceException_24133056 ex) { return -ex.getStatus(); }
    }
    private List<Long> concurrent(Callable<Long> a, Callable<Long> b) throws Exception {
        CountDownLatch ready = new CountDownLatch(2), start = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            List<Future<Long>> results = new ArrayList<>();
            for (var action : List.of(a, b)) results.add(pool.submit(() -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) throw new TimeoutException();
                return action.call();
            }));
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            return List.of(results.getFirst().get(15, TimeUnit.SECONDS), results.getLast().get(15, TimeUnit.SECONDS));
        }
    }
    private int stock(long id) {
        try (var em = factory.createEntityManager()) { return em.find(Product_24133056.class, id).getStock(); }
    }
    private long countOrders() {
        try (var em = factory.createEntityManager()) {
            return em.createQuery("select count(o) from ShopOrder_24133056 o", Long.class).getSingleResult();
        }
    }
    private void sql(String sql) { tx(em -> em.createNativeQuery(sql).executeUpdate()); }
    private void tx(Consumer<EntityManager> work) {
        try (var em = factory.createEntityManager()) {
            var tx = em.getTransaction(); tx.begin();
            try { work.accept(em); tx.commit(); }
            catch (RuntimeException ex) { if (tx.isActive()) tx.rollback(); throw ex; }
        }
    }
    private static void business(int status, org.junit.jupiter.api.function.Executable action) {
        assertEquals(status, assertThrows(CommerceException_24133056.class, action).getStatus());
    }
}
