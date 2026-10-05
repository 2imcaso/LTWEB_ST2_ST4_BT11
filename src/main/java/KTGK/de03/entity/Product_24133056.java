package KTGK.de03.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "shop_products")
@Check(constraints = "stock >= 0 and price > 0")
public class Product_24133056 {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String sku;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal price;
    @Column(nullable = false)
    private int stock;
    @Column(nullable = false)
    private boolean active;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "video_id", unique = true)
    private Video_24133056 video;
    @Version
    private long version;

    protected Product_24133056() { }

    public Product_24133056(String sku, String name, BigDecimal price, int stock) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.active = true;
    }

    public Product_24133056(Video_24133056 video, BigDecimal price, int stock) {
        this("VIDEO-" + video.getVideoId(), video.getTitle(), price, stock);
        this.video = video;
    }

    public void updateFromVideo(Video_24133056 video, BigDecimal price, int stock, boolean active) {
        if (video == null || price == null || price.signum() <= 0 || stock < 0)
            throw new IllegalArgumentException("Thông tin bán video không hợp lệ.");
        this.video = video;
        this.sku = "VIDEO-" + video.getVideoId();
        this.name = video.getTitle();
        this.price = price;
        this.stock = stock;
        this.active = active;
    }

    public void reserve(int quantity) {
        if (!active || quantity < 1 || quantity > stock)
            throw new IllegalStateException("Sản phẩm không còn đủ hàng.");
        stock -= quantity;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public boolean isActive() { return active; }
    public Video_24133056 getVideo() { return video; }
    public String getPoster() { return video == null ? null : video.getPoster(); }
    public String getVideoId() { return video == null ? null : video.getVideoId(); }
    public boolean isVideoProduct() { return video != null; }
    public long getVersion() { return version; }
}
