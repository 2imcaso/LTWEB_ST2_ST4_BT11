package KTGK.de03.service;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public final class Cart_24133056 implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String owner;
    private final Map<Long, Integer> items = new TreeMap<>();
    private long revision;

    public Cart_24133056(String owner) { this.owner = owner; }
    public String getOwner() { return owner; }
    public synchronized long getRevision() { return revision; }
    public synchronized Map<Long, Integer> getItems() { return Map.copyOf(items); }

    synchronized void requireRevision(long expected) {
        if (revision != expected)
            throw new CommerceException_24133056(409, "Giỏ hàng đã thay đổi ở một thao tác khác. Vui lòng kiểm tra lại.");
    }
    synchronized void put(long productId, int quantity) {
        items.put(productId, quantity);
        revision++;
    }
    synchronized void remove(long productId) {
        if (items.remove(productId) == null)
            throw new CommerceException_24133056(404, "Sản phẩm không có trong giỏ hàng.");
        revision++;
    }
    synchronized void clear() {
        items.clear();
        revision++;
    }
}
