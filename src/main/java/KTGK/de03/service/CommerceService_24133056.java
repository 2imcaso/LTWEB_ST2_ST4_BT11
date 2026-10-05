package KTGK.de03.service;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import KTGK.de03.dao.CommerceDao_24133056;
import KTGK.de03.dao.JpaUtil_24133056;
import KTGK.de03.entity.*;
import KTGK.de03.service.CommerceView_24133056.*;
import KTGK.de03.service.CommerceValidation_24133056.Shipping;

public final class CommerceService_24133056 {
    private static final BigDecimal MAX_TOTAL = new BigDecimal("999999999999999999999999");
    private final Supplier<EntityManager> managers;
    private final Clock clock;
    private final CommerceDao_24133056 dao = new CommerceDao_24133056();

    public CommerceService_24133056() {
        this(JpaUtil_24133056::em, Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")));
    }
    public CommerceService_24133056(Supplier<EntityManager> managers, Clock clock) {
        this.managers = managers;
        this.clock = clock;
    }

    public Page<Product_24133056> products(int requestedPage) {
        try (EntityManager em = managers.get()) {
            long count = dao.productCount(em);
            int page = boundedPage(requestedPage, count, 12);
            return new Page<>(dao.products(em, (page - 1) * 12, 12), page, count, 12);
        }
    }

    public Optional<Product_24133056> productForVideo(String videoId) {
        if (videoId == null || videoId.isBlank()) return Optional.empty();
        try (EntityManager em = managers.get()) {
            return dao.productByVideo(em, videoId);
        }
    }

    public Map<String, Product_24133056> productsForVideos(Collection<String> videoIds) {
        try (EntityManager em = managers.get()) {
            Map<String, Product_24133056> result = new HashMap<>();
            for (Product_24133056 product : dao.productsByVideo(em, videoIds))
                result.put(product.getVideoId(), product);
            return result;
        }
    }

    public void saveVideoProduct(String videoId, BigDecimal price, int stock, boolean active) {
        if (videoId == null || videoId.isBlank() || price == null || price.signum() <= 0 || stock < 0)
            throw CommerceValidation_24133056.invalid("Giá video phải lớn hơn 0 và số lượt bán không được âm.");
        try (EntityManager em = managers.get()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Video_24133056 video = em.find(Video_24133056.class, videoId);
                if (video == null) throw new IllegalArgumentException("Video không tồn tại.");
                Product_24133056 product = dao.productByVideo(em, videoId)
                        .orElseGet(() -> new Product_24133056(video, price, stock));
                product.updateFromVideo(video, price, stock, active);
                if (product.getId() == null) em.persist(product);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public void deleteVideoProduct(String videoId) {
        try (EntityManager em = managers.get()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                dao.productByVideo(em, videoId).ifPresent(em::remove);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public void changeCart(String username, Cart_24133056 cart, long revision, String action,
            long productId, int quantity) {
        if (action == null) throw CommerceValidation_24133056.invalid("Thiếu thao tác giỏ hàng.");
        synchronized (cart) {
            requireOwner(username, cart);
            cart.requireRevision(revision);
            try (EntityManager em = managers.get()) {
                requireUser(em, username, false);
                switch (action) {
                    case "clear" -> cart.clear();
                    case "remove" -> cart.remove(productId);
                    case "add", "update" -> {
                        if (productId < 1 || quantity < 1)
                            throw CommerceValidation_24133056.invalid("Sản phẩm và số lượng phải hợp lệ.");
                        if ("update".equals(action) && !cart.getItems().containsKey(productId))
                            throw new CommerceException_24133056(404, "Sản phẩm không có trong giỏ hàng.");
                        long desired = quantity;
                        if ("add".equals(action)) desired += (long) cart.getItems().getOrDefault(productId, 0);
                        Product_24133056 product = dao.product(em, productId, false);
                        requireAvailable(product, desired);
                        cart.put(productId, (int) desired);
                    }
                    default -> throw CommerceValidation_24133056.invalid("Thao tác giỏ hàng không hợp lệ.");
                }
            }
        }
    }

    public CartView cart(String username, Cart_24133056 cart) {
        synchronized (cart) {
            requireOwner(username, cart);
            try (EntityManager em = managers.get()) {
                requireUser(em, username, false);
                List<Line> lines = new ArrayList<>();
                for (var entry : new TreeMap<>(cart.getItems()).entrySet()) {
                    Product_24133056 p = dao.product(em, entry.getKey(), false);
                    boolean usable = p != null && p.isActive() && p.getStock() >= entry.getValue()
                            && validPrice(p.getPrice());
                    lines.add(new Line(entry.getKey(), p == null ? "Sản phẩm không còn tồn tại" : p.getName(),
                            p == null ? BigDecimal.ZERO : p.getPrice(), entry.getValue(),
                            p == null || !p.isActive() ? 0 : p.getStock(), usable,
                            p == null ? null : p.getPoster(), p == null ? null : p.getVideoId()));
                }
                return new CartView(lines, cart.getRevision());
            }
        }
    }

    public Quote quote(String username, Cart_24133056 cart) {
        CartView view = cart(username, cart);
        if (!view.isCheckoutAllowed())
            throw new CommerceException_24133056(409, "Giỏ hàng trống hoặc có sản phẩm không còn đủ hàng. Vui lòng cập nhật giỏ hàng.");
        if (view.getTotal().compareTo(MAX_TOTAL) > 0)
            throw CommerceValidation_24133056.invalid("Tổng tiền vượt giới hạn lưu trữ. Vui lòng giảm số lượng.");
        return new Quote(username, view);
    }

    public Optional<Long> completedCheckout(String username, String token) {
        requireToken(token);
        try (EntityManager em = managers.get()) {
            requireUser(em, username, false);
            return dao.byToken(em, username, token).map(ShopOrder_24133056::getId);
        }
    }

    public long checkout(String username, Cart_24133056 cart, Quote quote, Shipping shipping, String payment) {
        if (!"COD".equals(payment))
            throw CommerceValidation_24133056.invalid("Chỉ hỗ trợ thanh toán khi nhận hàng (COD).");
        if (quote == null || shipping == null || !Objects.equals(username, quote.getOwner()))
            throw new CommerceException_24133056(409, "Thông tin đặt hàng đã hết hiệu lực. Vui lòng kiểm tra lại giỏ hàng.");
        synchronized (cart) {
            requireOwner(username, cart);
            try (EntityManager em = managers.get()) {
                EntityTransaction tx = em.getTransaction();
                try {
                    tx.begin();
                    // Serialize checkouts per account, including retries arriving on different sessions.
                    User_24133056 user = requireUser(em, username, true);
                    var existing = dao.byToken(em, username, quote.getToken());
                    if (existing.isPresent()) {
                        long id = existing.get().getId();
                        tx.commit();
                        return id;
                    }
                    cart.requireRevision(quote.getRevision());
                    Map<Long, Integer> quantities = cart.getItems();
                    if (quantities.isEmpty() || quantities.size() != quote.getLines().size())
                        throw new CommerceException_24133056(409, "Giỏ hàng đã thay đổi. Vui lòng kiểm tra lại.");
                    ShopOrder_24133056 order = new ShopOrder_24133056(user, quote.getToken(), shipping.recipient(),
                            shipping.phone(), shipping.address(), shipping.note(), LocalDateTime.now(clock));
                    // Every checkout locks products in the same order to avoid crossed-cart deadlocks.
                    List<Line> lines = quote.getLines().stream().sorted(Comparator.comparingLong(Line::getProductId)).toList();
                    for (Line line : lines) {
                        if (!Objects.equals(quantities.get(line.getProductId()), line.getQuantity()))
                            throw new CommerceException_24133056(409, "Số lượng trong giỏ đã thay đổi.");
                        Product_24133056 product = dao.product(em, line.getProductId(), true);
                        requireAvailable(product, line.getQuantity());
                        if (product.getPrice().compareTo(line.getPrice()) != 0)
                            throw new CommerceException_24133056(409, "Giá sản phẩm đã thay đổi. Vui lòng xem lại tổng tiền trước khi đặt hàng.");
                        order.addItem(product, line.getQuantity());
                        product.reserve(line.getQuantity());
                    }
                    if (order.getTotal().compareTo(MAX_TOTAL) > 0)
                        throw CommerceValidation_24133056.invalid("Tổng tiền vượt giới hạn lưu trữ.");
                    em.persist(order);
                    tx.commit();
                    // Never clear the cart before the database commit succeeds.
                    cart.clear();
                    return order.getId();
                } catch (RuntimeException ex) {
                    if (tx.isActive()) tx.rollback();
                    if (ex instanceof OptimisticLockException || ex instanceof PessimisticLockException
                            || ex instanceof LockTimeoutException)
                        throw new CommerceException_24133056(409, "Dữ liệu đang được cập nhật. Vui lòng kiểm tra lại và thử lại.");
                    throw ex;
                }
            }
        }
    }

    public Page<ShopOrder_24133056> orders(String username, String filter, int requestedPage) {
        OrderStatus_24133056 status = CommerceValidation_24133056.status(filter);
        try (EntityManager em = managers.get()) {
            requireUser(em, username, false);
            long count = dao.orderCount(em, username, status);
            int page = boundedPage(requestedPage, count, 10);
            return new Page<>(dao.orders(em, username, status, (page - 1) * 10, 10), page, count, 10);
        }
    }

    public ShopOrder_24133056 order(String username, long id) {
        try (EntityManager em = managers.get()) {
            requireUser(em, username, false);
            return dao.order(em, username, id)
                    .orElseThrow(() -> new CommerceException_24133056(404, "Không tìm thấy đơn hàng."));
        }
    }

    private User_24133056 requireUser(EntityManager em, String username, boolean lock) {
        if (username == null || username.isBlank())
            throw new CommerceException_24133056(403, "Vui lòng đăng nhập.");
        return dao.user(em, username, lock).filter(User_24133056::isActive)
                .orElseThrow(() -> new CommerceException_24133056(403, "Tài khoản không còn hoạt động. Vui lòng đăng nhập lại."));
    }
    private void requireOwner(String username, Cart_24133056 cart) {
        if (username == null || !username.equals(cart.getOwner()))
            throw new CommerceException_24133056(403, "Bạn không có quyền truy cập giỏ hàng này.");
    }
    private boolean validPrice(BigDecimal price) {
        return price != null && price.signum() > 0 && price.stripTrailingZeros().scale() <= 0;
    }
    private void requireAvailable(Product_24133056 product, long quantity) {
        if (product == null || !product.isActive() || !validPrice(product.getPrice()))
            throw new CommerceException_24133056(409, "Sản phẩm không còn được bán.");
        if (quantity < 1 || quantity > product.getStock())
            throw new CommerceException_24133056(409, "Số lượng vượt tồn kho hiện tại (" + product.getStock() + ").");
    }
    private int boundedPage(int requested, long count, int size) {
        if (requested < 1 || requested > Integer.MAX_VALUE / size)
            throw CommerceValidation_24133056.invalid("Số trang không hợp lệ.");
        return (int) Math.min(requested, Math.max(1, (count + size - 1) / size));
    }
    public static void requireToken(String token) {
        if (token == null || !token.matches("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}"))
            throw new CommerceException_24133056(409, "Phiếu đặt hàng không hợp lệ. Vui lòng mở lại trang thanh toán.");
    }
}
