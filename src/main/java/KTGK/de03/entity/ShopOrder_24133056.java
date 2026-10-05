package KTGK.de03.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "shop_orders", indexes = {
    @Index(name = "idx_shop_orders_customer_status", columnList = "customer_username,status,id")
})
@Check(constraints = "total > 0 and payment_method = 'COD' and status in ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')")
public class ShopOrder_24133056 {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_username", nullable = false)
    private User_24133056 customer;
    @Column(name = "checkout_token", nullable = false, unique = true, length = 36)
    private String checkoutToken;
    @Column(nullable = false, length = 100)
    private String recipient;
    @Column(nullable = false, length = 20)
    private String phone;
    @Column(nullable = false, length = 500)
    private String address;
    @Column(nullable = false, length = 1000)
    private String note;
    @Column(name = "payment_method", nullable = false, length = 10)
    private String paymentMethod = "COD";
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32, columnDefinition = "varchar(32)")
    private OrderStatus_24133056 status = OrderStatus_24133056.NEW;
    @Column(nullable = false, precision = 24, scale = 0)
    private BigDecimal total = BigDecimal.ZERO;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Version
    private long version;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @OrderBy("id")
    private List<OrderItem_24133056> items = new ArrayList<>();

    protected ShopOrder_24133056() { }

    public ShopOrder_24133056(User_24133056 customer, String token, String recipient,
            String phone, String address, String note, LocalDateTime createdAt) {
        this.customer = customer;
        this.checkoutToken = token;
        this.recipient = recipient;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.createdAt = createdAt;
    }

    public void addItem(Product_24133056 product, int quantity) {
        OrderItem_24133056 item = new OrderItem_24133056(this, product, quantity);
        items.add(item);
        total = total.add(item.getSubtotal());
    }

    public Long getId() { return id; }
    public String getRecipient() { return recipient; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getNote() { return note; }
    public String getPaymentMethod() { return paymentMethod; }
    public OrderStatus_24133056 getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCreatedAtDisplay() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public long getVersion() { return version; }
    public List<OrderItem_24133056> getItems() { return List.copyOf(items); }
}
