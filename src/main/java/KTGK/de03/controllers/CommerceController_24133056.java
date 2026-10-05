package KTGK.de03.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import KTGK.de03.entity.OrderStatus_24133056;
import KTGK.de03.entity.User_24133056;
import KTGK.de03.service.*;
import KTGK.de03.service.CommerceValidation_24133056.Shipping;

@WebServlet(urlPatterns = {"/products", "/cart", "/checkout", "/orders", "/orders/detail"})
public class CommerceController_24133056 extends HttpServlet {
    private final CommerceService_24133056 service;
    public CommerceController_24133056() { this(new CommerceService_24133056()); }
    CommerceController_24133056(CommerceService_24133056 service) { this.service = service; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        setSection(request);
        User_24133056 user = user(request);
        if (user == null && !"/products".equals(request.getServletPath())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            if (user == null) {
                request.setAttribute("catalog", service.products(page(request)));
                render(request, response, "products");
            } else {
                synchronized (request.getSession()) {
                    var state = CommerceSession_24133056.get(request.getSession(), user.getUsername());
                    prepare(request, state);
                    switch (request.getServletPath()) {
                        case "/products" -> {
                            request.setAttribute("catalog", service.products(page(request)));
                            request.setAttribute("revision", state.cart().getRevision());
                            render(request, response, "products");
                        }
                        case "/cart" -> cart(request, response, user.getUsername(), state);
                        case "/checkout" -> checkout(request, response, user.getUsername(), state);
                        case "/orders" -> {
                            String status = parameter(request, "status");
                            request.setAttribute("selectedStatus", status == null ? "" : status);
                            request.setAttribute("statuses", OrderStatus_24133056.values());
                            request.setAttribute("history", service.orders(user.getUsername(), status, page(request)));
                            render(request, response, "orders");
                        }
                        case "/orders/detail" -> {
                            long id = CommerceValidation_24133056.number(parameter(request, "id"), "Mã đơn", 1, Long.MAX_VALUE);
                            request.setAttribute("order", service.order(user.getUsername(), id));
                            render(request, response, "order-detail");
                        }
                        default -> response.sendError(404);
                    }
                }
            }
        } catch (CommerceException_24133056 ex) {
            error(request, response, ex.getStatus(), ex.getMessage());
        } catch (RuntimeException ex) {
            unavailable(request, response, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        setSection(request);
        String path = request.getServletPath();
        if (!"/cart".equals(path) && !"/checkout".equals(path)) {
            response.setHeader("Allow", "GET");
            response.sendError(405);
            return;
        }
        User_24133056 user = user(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        synchronized (request.getSession()) {
            var state = CommerceSession_24133056.get(request.getSession(), user.getUsername());
            prepare(request, state);
            try {
                state.checkCsrf(parameter(request, "csrf"));
                if ("/cart".equals(path)) {
                    String action = parameter(request, "action");
                    if (action == null) throw CommerceValidation_24133056.invalid("Thiếu thao tác giỏ hàng.");
                    String revision = parameter(request, "revision");
                    if (revision == null || revision.isBlank())
                        throw new CommerceException_24133056(409, "Thiếu phiên bản giỏ hàng. Vui lòng tải lại trang.");
                    long expected = CommerceValidation_24133056.number(revision, "Phiên bản giỏ hàng", 0, Long.MAX_VALUE);
                    long productId = "clear".equals(action) ? 0
                            : CommerceValidation_24133056.number(parameter(request, "productId"), "Mã sản phẩm", 1, Long.MAX_VALUE);
                    int quantity = "add".equals(action) || "update".equals(action)
                            ? CommerceValidation_24133056.quantity(parameter(request, "quantity")) : 0;
                    service.changeCart(user.getUsername(), state.cart(), expected, action, productId, quantity);
                    state.quote(null);
                    state.message("Đã cập nhật giỏ hàng.");
                    redirect(request, response, "/cart");
                } else {
                    String token = parameter(request, "checkoutToken");
                    CommerceService_24133056.requireToken(token);
                    var completed = service.completedCheckout(user.getUsername(), token);
                    if (completed.isPresent()) {
                        redirect(request, response, "/orders/detail?id=" + completed.get());
                        return;
                    }
                    if (state.quote() == null || !state.quote().getToken().equals(token))
                        throw new CommerceException_24133056(409, "Phiếu đặt hàng đã thay đổi. Vui lòng kiểm tra và xác nhận lại.");
                    Shipping shipping = new Shipping(parameter(request, "recipient"), parameter(request, "phone"),
                            parameter(request, "address"), parameter(request, "note"));
                    long id = service.checkout(user.getUsername(), state.cart(), state.quote(), shipping,
                            parameter(request, "paymentMethod"));
                    state.quote(null);
                    state.message("Đặt hàng thành công. Bạn sẽ thanh toán khi nhận hàng (COD).");
                    redirect(request, response, "/orders/detail?id=" + id);
                }
            } catch (CommerceException_24133056 ex) {
                response.setStatus(ex.getStatus());
                request.setAttribute("error", ex.getMessage());
                if (ex.getStatus() == 403) {
                    render(request, response, "commerce-error");
                    return;
                }
                try {
                    if ("/checkout".equals(path) && !state.cart().getItems().isEmpty()) {
                        checkout(request, response, user.getUsername(), state);
                    } else {
                        cart(request, response, user.getUsername(), state);
                    }
                } catch (CommerceException_24133056 invalidCart) {
                    error(request, response, invalidCart.getStatus(), ex.getMessage() + " " + invalidCart.getMessage());
                } catch (RuntimeException failure) {
                    unavailable(request, response, failure);
                }
            } catch (RuntimeException ex) {
                unavailable(request, response, ex);
            }
        }
    }

    private void prepare(HttpServletRequest request, CommerceSession_24133056 state) {
        request.setAttribute("csrf", state.csrf());
        request.setAttribute("message", state.takeMessage());
    }
    private void setSection(HttpServletRequest request) {
        String path = request.getServletPath();
        request.setAttribute("shopSection", path.startsWith("/orders") ? "orders" : path.substring(1));
    }
    private void cart(HttpServletRequest request, HttpServletResponse response, String username,
            CommerceSession_24133056 state) throws ServletException, IOException {
        request.setAttribute("cart", service.cart(username, state.cart()));
        render(request, response, "cart");
    }
    private void checkout(HttpServletRequest request, HttpServletResponse response, String username,
            CommerceSession_24133056 state) throws ServletException, IOException {
        var quote = service.quote(username, state.cart());
        state.quote(quote);
        request.setAttribute("quote", quote);
        render(request, response, "checkout");
    }
    private User_24133056 user(HttpServletRequest request) {
        var session = request.getSession(false);
        return session == null ? null : (User_24133056) session.getAttribute("user");
    }
    private int page(HttpServletRequest request) {
        String input = parameter(request, "page");
        return input == null ? 1 : (int) CommerceValidation_24133056.number(input, "Số trang", 1, Integer.MAX_VALUE / 12);
    }
    private String parameter(HttpServletRequest request, String name) {
        String[] values = request.getParameterValues(name);
        if (values == null) return null;
        if (values.length != 1) throw CommerceValidation_24133056.invalid("Tham số " + name + " bị lặp.");
        return values[0];
    }
    private void render(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(request, response);
    }
    private void redirect(HttpServletRequest request, HttpServletResponse response, String path) {
        response.setStatus(303);
        response.setHeader("Location", request.getContextPath() + path);
    }
    private void error(HttpServletRequest request, HttpServletResponse response, int status, String message)
            throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("error", message);
        render(request, response, "commerce-error");
    }
    private void unavailable(HttpServletRequest request, HttpServletResponse response, RuntimeException ex)
            throws ServletException, IOException {
        getServletContext().log("Commerce request failed: " + request.getServletPath(), ex);
        error(request, response, 503, "Chưa thể xử lý yêu cầu. Vui lòng tải lại trang và thử lại; không cần gửi lại liên tục.");
    }
}
