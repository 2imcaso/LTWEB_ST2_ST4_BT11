package KTGK.de03.entity;

public enum OrderStatus_24133056 {
    NEW("Đơn hàng mới"),
    CONFIRMED("Đã xác nhận"),
    PREPARING("Chuẩn bị hàng"),
    SHIPPING("Vận chuyển"),
    DELIVERING("Giao hàng"),
    DELIVERED("Đã giao"),
    CANCELLED("Đơn hàng hủy"),
    RETURNED("Đơn hàng hoàn");

    private final String label;
    OrderStatus_24133056(String label) { this.label = label; }
    public String getLabel() { return label; }
    public String getCode() { return name(); }
}
