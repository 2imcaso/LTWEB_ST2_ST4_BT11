package KTGK.de03.controllers;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.regex.Pattern;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.*;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import KTGK.de03.entity.*;
import KTGK.de03.service.CommerceService_24133056;

/** Real HTTP, servlet, JSP, JSTL and SiteMesh checks against an isolated database. */
class CommerceWebTest {
    private static EntityManagerFactory factory;
    private static Tomcat tomcat;
    private static String base;
    private static long productId;
    private HttpClient client;

    @BeforeAll static void start() throws Exception {
        factory = new Configuration().addAnnotatedClass(User_24133056.class)
                .addAnnotatedClass(Category_24133056.class).addAnnotatedClass(Video_24133056.class)
                .addAnnotatedClass(Product_24133056.class).addAnnotatedClass(ShopOrder_24133056.class)
                .addAnnotatedClass(OrderItem_24133056.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:commerce-web;MODE=MySQL;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop").buildSessionFactory();
        try (var em = factory.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new User_24133056("alice", "test", "Nguyễn <script>alert(1)</script>", "alice@example.test", false, true));
            em.persist(new User_24133056("bob", "test", "Bob", "bob@example.test", false, true));
            em.persist(new User_24133056("admin", "test", "Quản trị viên", "admin@example.test", true, true));
            var p = new Product_24133056("WEB", "Sách <script>alert(1)</script>", new BigDecimal("100000"), 100);
            em.persist(p); em.flush(); productId = p.getId(); em.getTransaction().commit();
        }
        Path root = Files.createTempDirectory(Path.of("target"), "commerce-web-");
        Path webapp = root.resolve("webapp");
        Path source = Path.of("src/main/webapp");
        try (var files = Files.walk(source)) {
            for (Path path : files.toList()) {
                Path relative = source.relativize(path);
                if (relative.toString().replace('\\', '/').equals("WEB-INF/web.xml")) continue;
                Path dest = webapp.resolve(relative);
                if (Files.isDirectory(path)) Files.createDirectories(dest);
                else Files.copy(path, dest);
            }
        }
        Files.writeString(webapp.resolve("WEB-INF/web.xml"), """
                <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0" metadata-complete="true">
                  <jsp-config><jsp-property-group><url-pattern>*.jsp</url-pattern><page-encoding>UTF-8</page-encoding></jsp-property-group></jsp-config>
                </web-app>
                """);
        tomcat = new Tomcat();
        tomcat.setBaseDir(root.resolve("tomcat").toAbsolutePath().toString());
        tomcat.setPort(0);
        tomcat.getConnector().setProperty("address", "127.0.0.1");
        var context = tomcat.addWebapp("/shop-test", webapp.toAbsolutePath().toString());
        context.setParentClassLoader(CommerceWebTest.class.getClassLoader());
        context.setIgnoreAnnotations(true);
        var service = new CommerceService_24133056(factory::createEntityManager, Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")));
        Tomcat.addServlet(context, "commerce", new CommerceController_24133056(service));
        for (String path : List.of("/products", "/cart", "/checkout", "/orders", "/orders/detail"))
            context.addServletMappingDecoded(path, "commerce");
        // Test-only session fixture: never present in the application WAR.
        Tomcat.addServlet(context, "test-login", new HttpServlet() {
            @Override protected void doGet(HttpServletRequest r, HttpServletResponse s) throws IOException {
                try (var em = factory.createEntityManager()) {
                    var user = em.find(User_24133056.class, r.getParameter("user"));
                    r.getSession().setAttribute("user", user);
                    s.getWriter().write("ok");
                }
            }
        });
        context.addServletMappingDecoded("/test-login", "test-login");
        // Render the target project's existing video views with empty fixture data.
        Tomcat.addServlet(context, "video-fixture", new HttpServlet() {
            @Override protected void doGet(HttpServletRequest r, HttpServletResponse s)
                    throws IOException, jakarta.servlet.ServletException {
                r.setAttribute("groups", Map.of()); r.setAttribute("counts", Map.of());
                r.setAttribute("categories", List.of()); r.setAttribute("videos", List.of());
                r.setAttribute("page", 1); r.setAttribute("totalPages", 1);
                r.setAttribute("adminSection", "videos");
                r.getRequestDispatcher("/WEB-INF/views/" + ("/admin".equals(r.getServletPath()) ? "admin" : "home") + ".jsp").forward(r, s);
            }
        });
        context.addServletMappingDecoded("/home", "video-fixture");
        context.addServletMappingDecoded("/admin", "video-fixture");
        FilterDef adminFilter = new FilterDef();
        adminFilter.setFilterClass(AdminFilter_24133056.class.getName());
        adminFilter.setFilterName("admin-access"); adminFilter.setFilter(new AdminFilter_24133056());
        context.addFilterDef(adminFilter);
        FilterMap adminMapping = new FilterMap();
        adminMapping.setFilterName("admin-access"); adminMapping.addURLPattern("/admin");
        context.addFilterMap(adminMapping);
        FilterDef filter = new FilterDef();
        filter.setFilterClass(ConfigurableSiteMeshFilter.class.getName());
        filter.setFilterName("sitemesh"); filter.setFilter(new ConfigurableSiteMeshFilter());
        context.addFilterDef(filter);
        FilterMap mapping = new FilterMap();
        mapping.setFilterName("sitemesh"); mapping.addURLPattern("/*"); context.addFilterMap(mapping);
        tomcat.start();
        assertTrue(context.getState().isAvailable(), "Isolated Tomcat context must start successfully");
        base = "http://127.0.0.1:" + tomcat.getConnector().getLocalPort() + "/shop-test";
    }

    @AfterAll static void stop() throws Exception {
        try { if (tomcat != null) { tomcat.stop(); tomcat.destroy(); } }
        finally { if (factory != null) factory.close(); }
    }

    @BeforeEach void browser() {
        client = HttpClient.newBuilder().cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NEVER).connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Test void anonymousCatalogAndProtectedPages() throws Exception {
        var products = get("/products");
        assertEquals(200, products.statusCode(), products.body());
        assertTrue(products.body().contains("Đăng nhập để mua"));
        assertTrue(products.body().contains("Sách &lt;script&gt;"));
        assertTrue(products.body().contains("class=\"site-header\""), "SiteMesh must render the VidStream header");
        assertTrue(products.body().contains("class=\"footer-grid\""), "SiteMesh must render the VidStream footer");
        assertTrue(products.body().contains("commerce.css"));
        assertFalse(products.body().contains("bootstrap"));
        assertTrue(products.body().contains("/shop-test/cart"), "Cart link must be discoverable before login too");
        for (String path : List.of("/cart", "/checkout", "/orders", "/orders/detail?id=1")) {
            var response = get(path);
            assertEquals(302, response.statusCode());
            assertTrue(response.headers().firstValue("Location").orElseThrow().endsWith("/login"));
        }
    }

    @Test void fullCodJourneyRejectsDuplicateSubmitAndProtectsOrderDetail() throws Exception {
        get("/test-login?user=alice");
        var catalog = get("/products");
        assertEquals(200, catalog.statusCode(), catalog.body());
        assertTrue(catalog.body().contains("/shop-test/cart"));
        assertFalse(catalog.body().contains("<script>alert(1)</script>"));
        var add = post("/cart", form(catalog.body(), "action", "add", "productId", "" + productId, "quantity", "2"));
        assertEquals(303, add.statusCode(), add.body());
        var cart = get("/cart");
        assertEquals(200, cart.statusCode(), cart.body());
        assertTrue(cart.body().contains("200.000"));
        var checkout = get("/checkout");
        assertEquals(200, checkout.statusCode(), checkout.body());
        assertTrue(checkout.body().contains("&lt;script&gt;"));
        Map<String, String> fields = form(checkout.body(), "recipient", "Nguyễn Văn A", "phone", "0901234567",
                "address", "12 Đường A", "note", "<script>alert('x')</script>", "paymentMethod", "COD",
                "total", "1", "price", "1", "customer", "bob", "status", "DELIVERED");
        var placed = post("/checkout", fields);
        assertEquals(303, placed.statusCode(), placed.body());
        String location = placed.headers().firstValue("Location").orElseThrow();
        var detail = get(location.substring("/shop-test".length()));
        assertEquals(200, detail.statusCode(), detail.body());
        assertTrue(detail.body().contains("200.000"));
        assertTrue(detail.body().contains("Đơn hàng mới"));
        assertTrue(detail.body().contains("&lt;script&gt;"));
        assertFalse(detail.body().contains("<script>alert('x')</script>"));
        var repeated = post("/checkout", fields);
        assertEquals(303, repeated.statusCode());
        assertEquals(location, repeated.headers().firstValue("Location").orElseThrow());
        assertTrue(get("/cart").body().contains("Giỏ hàng đang trống"));
        var history = get("/orders?status=NEW");
        assertEquals(200, history.statusCode(), history.body());
        assertTrue(history.body().contains("Đơn hàng mới"));
        get("/test-login?user=bob");
        assertEquals(404, get(location.substring("/shop-test".length())).statusCode());
    }

    @Test void csrfMissingRevisionAndStaleCartRejected() throws Exception {
        get("/test-login?user=alice");
        var catalog = get("/products");
        var fields = form(catalog.body(), "action", "add", "productId", "" + productId, "quantity", "1");
        String csrf = fields.remove("csrf");
        assertEquals(403, post("/cart", fields).statusCode());
        fields.put("csrf", csrf);
        String revision = fields.remove("revision");
        assertEquals(409, post("/cart", fields).statusCode());
        fields.put("revision", revision);
        assertEquals(303, post("/cart", fields).statusCode());
        assertEquals(409, post("/cart", fields).statusCode());
        assertEquals(405, post("/orders", Map.of()).statusCode());
    }

    @Test void invalidQuantityAndContactRemainRecoverableAndEscaped() throws Exception {
        get("/test-login?user=alice");
        var catalog = get("/products");
        var bad = form(catalog.body(), "action", "add", "productId", "" + productId, "quantity", "-1");
        assertEquals(400, post("/cart", bad).statusCode());
        bad.put("quantity", "1");
        assertEquals(303, post("/cart", bad).statusCode());
        var checkout = get("/checkout");
        var fields = form(checkout.body(), "recipient", "\"><script>alert(2)</script>", "phone", "bad",
                "address", "Địa chỉ", "note", "", "paymentMethod", "COD");
        var response = post("/checkout", fields);
        assertEquals(400, response.statusCode(), response.body());
        assertTrue(response.body().contains("Số điện thoại"));
        assertFalse(response.body().contains("\"><script>alert(2)</script>"));
        assertTrue(response.body().contains("&lt;script&gt;"));
        assertEquals(400, get("/orders?status=UNKNOWN").statusCode());
        assertEquals(400, get("/orders?page=1&page=2").statusCode());
        assertEquals(400, get("/orders/detail?id=-1").statusCode());
    }

    @Test void switchingAccountsDoesNotShareCartOrCsrf() throws Exception {
        get("/test-login?user=alice");
        var catalog = get("/products");
        var fields = form(catalog.body(), "action", "add", "productId", "" + productId, "quantity", "1");
        assertEquals(303, post("/cart", fields).statusCode());
        get("/test-login?user=bob");
        assertTrue(get("/cart").body().contains("Giỏ hàng đang trống"));
        assertEquals(403, post("/cart", fields).statusCode());
    }

    @Test void existingVideoHomeAndAdminKeepTheirTemplateAndExposeShopLinks() throws Exception {
        var home = get("/home");
        assertEquals(200, home.statusCode(), home.body());
        assertTrue(home.body().contains("class=\"hero\""));
        assertTrue(home.body().contains("/shop-test/products"));
        assertTrue(home.body().contains("/shop-test/cart"));
        assertEquals(302, get("/admin").statusCode());
        get("/test-login?user=alice");
        assertEquals(302, get("/admin").statusCode());
        get("/test-login?user=admin");
        var admin = get("/admin");
        assertEquals(200, admin.statusCode(), admin.body());
        assertTrue(admin.body().contains("CMS Studio"));
        assertTrue(admin.body().contains("/shop-test/products"));
        assertTrue(admin.body().contains("/shop-test/cart"));
        assertTrue(admin.body().contains("Đơn mua của tôi"));
        assertTrue(admin.body().contains("/shop-test/admin/videos"));
    }

    /** Optional isolated visual preview; no production database or authentication is used. */
    public static void main(String[] args) throws Exception {
        start();
        try (var em = factory.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("update Product_24133056 set name='Sách lập trình Java', price=120000, stock=20").executeUpdate();
            em.createQuery("update User_24133056 set fullname='Nguyễn Văn An' where username='alice'").executeUpdate();
            em.persist(new Product_24133056("NOTEBOOK", "Sổ ghi chép", new BigDecimal("35000"), 15));
            em.persist(new Product_24133056("PEN", "Bút bi", new BigDecimal("10000"), 5));
            em.getTransaction().commit();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { stop(); } catch (Exception ex) { ex.printStackTrace(); }
        }));
        System.out.println("PREVIEW_URL=" + base);
        new java.util.concurrent.CountDownLatch(1).await();
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + path)).timeout(Duration.ofSeconds(30)).GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
    private HttpResponse<String> post(String path, Map<String, String> fields) throws Exception {
        String data = fields.entrySet().stream().map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)).collect(java.util.stream.Collectors.joining("&"));
        return client.send(HttpRequest.newBuilder(URI.create(base + path)).timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(data)).build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
    private Map<String, String> form(String html, String... overrides) {
        Map<String, String> fields = new HashMap<>();
        var pattern = Pattern.compile("<input[^>]*type=\"hidden\"[^>]*name=\"([^\"]+)\"[^>]*value=\"([^\"]*)\"");
        var matcher = pattern.matcher(html);
        while (matcher.find()) fields.putIfAbsent(matcher.group(1), matcher.group(2));
        for (int i = 0; i < overrides.length; i += 2) fields.put(overrides[i], overrides[i + 1]);
        return fields;
    }
}
