package KTGK.de03.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "shop_order_items", uniqueConstraints = {
    @UniqueConstraint(name = "uk_shop_order_product", columnNames = {"order_id", "product_id"})
})
@Check(constraints = "quantity > 0 and unit_price > 0")
public class OrderItem_24133056 {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private ShopOrder_24133056 order;
    // Snapshot identifiers, without a product FK: old orders survive catalog changes/deletion.
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(nullable = false, length = 50)
    private String sku;
    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 0)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private int quantity;

    protected OrderItem_24133056() { }

    public OrderItem_24133056(ShopOrder_24133056 order, Product_24133056 product, int quantity) {
        this.order = order;
        this.productId = product.getId();
        this.sku = product.getSku();
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public BigDecimal getSubtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
