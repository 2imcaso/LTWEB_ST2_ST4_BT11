package KTGK.de03.controllers;

import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import KTGK.de03.service.Cart_24133056;
import KTGK.de03.service.CommerceException_24133056;
import KTGK.de03.service.CommerceView_24133056.Quote;

final class CommerceSession_24133056 implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String owner;
    private final String csrf = UUID.randomUUID().toString();
    private final Cart_24133056 cart;
    private Quote quote;
    private String message;

    private CommerceSession_24133056(String owner) {
        this.owner = owner;
        this.cart = new Cart_24133056(owner);
    }

    static CommerceSession_24133056 get(HttpSession session, String owner) {
        synchronized (session) {
            var state = (CommerceSession_24133056) session.getAttribute("commerce");
            if (state == null || !state.owner.equals(owner)) {
                state = new CommerceSession_24133056(owner);
                session.setAttribute("commerce", state);
            }
            return state;
        }
    }

    void checkCsrf(String received) {
        if (received == null || !MessageDigest.isEqual(csrf.getBytes(StandardCharsets.UTF_8),
                received.getBytes(StandardCharsets.UTF_8)))
            throw new CommerceException_24133056(403, "Phiên thao tác không hợp lệ. Vui lòng tải lại trang.");
    }
    String csrf() { return csrf; }
    Cart_24133056 cart() { return cart; }
    Quote quote() { return quote; }
    void quote(Quote quote) { this.quote = quote; }
    void message(String message) { this.message = message; }
    String takeMessage() {
        String value = message;
        message = null;
        return value;
    }
}
