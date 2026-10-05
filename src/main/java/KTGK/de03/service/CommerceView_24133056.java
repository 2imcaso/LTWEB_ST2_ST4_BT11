package KTGK.de03.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class CommerceView_24133056 {
    private CommerceView_24133056() { }

    public static final class Line implements Serializable {
        private static final long serialVersionUID = 1L;
        private final long productId;
        private final String name;
        private final BigDecimal price;
        private final int quantity;
        private final int stock;
        private final boolean available;
        private final String poster;
        private final String videoId;

        public Line(long productId, String name, BigDecimal price, int quantity, int stock, boolean available,
                String poster, String videoId) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.stock = stock;
            this.available = available;
            this.poster = poster;
            this.videoId = videoId;
        }
        public long getProductId() { return productId; }
        public String getName() { return name; }
        public BigDecimal getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public int getStock() { return stock; }
        public boolean isAvailable() { return available; }
        public String getPoster() { return poster; }
        public String getVideoId() { return videoId; }
        public BigDecimal getSubtotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
    }

    public static final class CartView {
        private final List<Line> lines;
        private final long revision;
        public CartView(List<Line> lines, long revision) {
            this.lines = List.copyOf(lines);
            this.revision = revision;
        }
        public List<Line> getLines() { return lines; }
        public long getRevision() { return revision; }
        public BigDecimal getTotal() {
            return lines.stream().map(Line::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        public boolean isCheckoutAllowed() {
            return !lines.isEmpty() && lines.stream().allMatch(Line::isAvailable);
        }
    }

    public static final class Quote implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String token = UUID.randomUUID().toString();
        private final String owner;
        private final long revision;
        private final List<Line> lines;
        Quote(String owner, CartView cart) {
            this.owner = owner;
            this.revision = cart.getRevision();
            this.lines = cart.getLines();
        }
        public String getToken() { return token; }
        public String getOwner() { return owner; }
        public long getRevision() { return revision; }
        public List<Line> getLines() { return lines; }
        public BigDecimal getTotal() {
            return lines.stream().map(Line::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    public static final class Page<T> {
        private final List<T> items;
        private final int page;
        private final long pages;
        public Page(List<T> items, int page, long total, int size) {
            this.items = List.copyOf(items);
            this.page = page;
            this.pages = Math.max(1, (total + size - 1) / size);
        }
        public List<T> getItems() { return items; }
        public int getPage() { return page; }
        public long getPages() { return pages; }
        public boolean isHasPrevious() { return page > 1; }
        public boolean isHasNext() { return page < pages; }
    }
}
