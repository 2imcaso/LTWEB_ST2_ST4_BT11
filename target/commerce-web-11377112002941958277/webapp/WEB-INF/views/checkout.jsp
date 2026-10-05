<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN" />
<!doctype html><html lang="vi"><head><title>Đặt hàng COD</title><%@ include file="/WEB-INF/fragments/shop/head.jspf" %></head><body>
<section class="shop-page">
    <div class="shop-heading"><div><p class="eyebrow">VIDSTREAM · ĐẶT HÀNG</p><h1>Hoàn tất đơn hàng</h1><p class="muted">Nhập thông tin nhận hàng và xác nhận đơn của bạn.</p></div><span class="badge">Thanh toán COD</span></div>
    <%@ include file="/WEB-INF/fragments/shop/messages.jspf" %>
    <div class="shop-layout shop-checkout-layout">
        <form class="panel shop-checkout-form" action="${pageContext.request.contextPath}/checkout" method="post">
            <h2>Thông tin nhận hàng</h2>
            <input type="hidden" name="csrf" value="${csrf}">
            <input type="hidden" name="checkoutToken" value="${quote.token}">
            <input type="hidden" name="paymentMethod" value="COD">
            <div class="shop-form-grid">
                <label>Họ tên người nhận<input name="recipient" maxlength="100" autocomplete="name" required value="${fn:escapeXml(pageContext.request.method == 'POST' ? param.recipient : sessionScope.user.fullname)}"></label>
                <label>Số điện thoại<input name="phone" type="tel" maxlength="16" pattern="\+?[0-9]{8,15}" autocomplete="tel" required aria-describedby="phone-help" value="${fn:escapeXml(pageContext.request.method == 'POST' ? param.phone : sessionScope.user.phone)}"><small id="phone-help">8–15 chữ số, có thể bắt đầu bằng +; không có dấu cách.</small></label>
                <label class="shop-field-wide">Địa chỉ nhận hàng<input name="address" maxlength="500" autocomplete="street-address" required value="${fn:escapeXml(param.address)}"></label>
                <label class="shop-field-wide">Ghi chú <span class="muted">(không bắt buộc)</span><input name="note" maxlength="1000" value="${fn:escapeXml(param.note)}"></label>
            </div>
            <div class="shop-payment"><span class="badge">COD</span><div><strong>Thanh toán khi nhận hàng</strong><p>Thanh toán tiền mặt cho người giao hàng khi nhận đơn.</p></div></div>
            <button class="button" type="submit">Xác nhận đặt hàng COD →</button>
            <a class="shop-back" href="${pageContext.request.contextPath}/cart">← Quay lại giỏ hàng</a>
        </form>
        <aside class="panel shop-summary" aria-label="Thông tin đơn hàng">
            <h2>Đơn hàng của bạn</h2>
            <c:forEach var="line" items="${quote.lines}"><div class="shop-review-line"><p><c:out value="${line.name}" /></p><div><span>${line.quantity} × <fmt:formatNumber value="${line.price}" maxFractionDigits="0" /> ₫</span><strong><fmt:formatNumber value="${line.subtotal}" maxFractionDigits="0" /> ₫</strong></div></div></c:forEach>
            <div class="shop-summary-row shop-total"><span>Tổng thanh toán</span><strong><fmt:formatNumber value="${quote.total}" maxFractionDigits="0" /> ₫</strong></div>
            <p class="muted">Vui lòng kiểm tra lại sản phẩm, số lượng và địa chỉ trước khi xác nhận.</p>
        </aside>
    </div>
</section>
</body></html>
