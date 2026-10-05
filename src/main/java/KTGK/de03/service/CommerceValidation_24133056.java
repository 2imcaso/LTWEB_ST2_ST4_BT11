package KTGK.de03.service;

import KTGK.de03.entity.OrderStatus_24133056;

public final class CommerceValidation_24133056 {
    private CommerceValidation_24133056() { }

    public static long number(String input, String label, long minimum, long maximum) {
        if (input == null || !input.matches("[0-9]{1,19}"))
            throw invalid(label + " phải là số nguyên hợp lệ.");
        try {
            long value = Long.parseLong(input);
            if (value < minimum || value > maximum) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException ex) {
            throw invalid(label + " nằm ngoài giới hạn cho phép.");
        }
    }

    public static int quantity(String input) {
        return (int) number(input, "Số lượng", 1, Integer.MAX_VALUE);
    }

    public static OrderStatus_24133056 status(String input) {
        if (input == null || input.isEmpty()) return null;
        try { return OrderStatus_24133056.valueOf(input); }
        catch (IllegalArgumentException ex) { throw invalid("Trạng thái đơn hàng không hợp lệ."); }
    }

    public static String text(String input, String label, int maximum, boolean required) {
        String value = input == null ? "" : input.strip();
        if (required && value.isBlank()) throw invalid("Vui lòng nhập " + label + ".");
        if (value.length() > maximum || value.chars().anyMatch(Character::isISOControl))
            throw invalid(label + " quá dài hoặc chứa ký tự điều khiển không hợp lệ.");
        return value;
    }

    public record Shipping(String recipient, String phone, String address, String note) {
        public Shipping {
            recipient = text(recipient, "họ tên người nhận", 100, true);
            phone = text(phone, "số điện thoại", 20, true);
            if (!phone.matches("\\+?[0-9]{8,15}"))
                throw invalid("Số điện thoại cần 8–15 chữ số, có thể bắt đầu bằng dấu +.");
            address = text(address, "địa chỉ nhận hàng", 500, true);
            note = text(note, "ghi chú", 1000, false);
        }
    }

    public static CommerceException_24133056 invalid(String message) {
        return new CommerceException_24133056(400, message);
    }
}
