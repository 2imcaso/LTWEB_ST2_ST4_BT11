<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN" />
<!doctype html><html lang="vi"><head><title>Giỏ hàng</title><%@ include file="/WEB-INF/fragments/shop/head.jspf" %></head><body>
<section class="shop-page">
    <div class="shop-heading"><div><p class="eyebrow">VIDSTREAM · GIỎ HÀNG</p><h1>Giỏ hàng của bạn</h1><p class="muted">Kiểm tra sản phẩm và số lượng trước khi đặt hàng.</p></div><a class="button secondary" href="${pageContext.request.contextPath}/products">← Tiếp tục mua hàng</a></div>
    <%@ include file="/WEB-INF/fragments/shop/messages.jspf" %>
    <c:choose><c:when test="${empty cart.lines}">
        <div class="panel shop-empty"><span class="shop-product-mark" aria-hidden="true">▣</span><h2>Giỏ hàng đang trống</h2><p>Khám phá cửa hàng và thêm những sản phẩm bạn muốn mua.</p><a class="button" href="${pageContext.request.contextPath}/products">Xem sản phẩm ↗</a></div>
    </c:when><c:otherwise>
        <div class="shop-layout">
            <div class="shop-cart-list">
                <c:forEach var="line" items="${cart.lines}">
                    <article class="panel shop-cart-line">
                        <div class="shop-cart-main"><c:if test="${not empty line.poster}"><a href="${pageContext.request.contextPath}/video?id=${line.videoId}"><img class="shop-cart-poster" src="<c:out value='${line.poster}'/>" alt=""></a></c:if><div><h2><c:out value="${line.name}" /></h2><p class="muted">Đơn giá: <fmt:formatNumber value="${line.price}" maxFractionDigits="0" /> ₫ · Còn ${line.stock}</p>
                            <c:if test="${not line.available}"><p class="error">Không còn đủ hàng hoặc đã ngừng bán. Cập nhật số lượng hoặc xóa sản phẩm.</p></c:if>
                        </div></div>
                        <c:choose><c:when test="${line.stock > 0}">
                            <form class="shop-quantity-form" action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="csrf" value="${csrf}">
                                <input type="hidden" name="revision" value="${cart.revision}">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="productId" value="${line.productId}">
                                <label>Số lượng<input name="quantity" type="number" min="1" max="${line.stock}" step="1" value="${line.quantity}" required></label>
                                <button class="secondary" type="submit">Cập nhật</button>
                            </form>
                        </c:when><c:otherwise><p class="muted">Số lượng: ${line.quantity}</p></c:otherwise></c:choose>
                        <div class="shop-line-total"><strong><fmt:formatNumber value="${line.subtotal}" maxFractionDigits="0" /> ₫</strong>
                            <form action="${pageContext.request.contextPath}/cart" method="post">
                                <input type="hidden" name="csrf" value="${csrf}">
                                <input type="hidden" name="revision" value="${cart.revision}">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productId" value="${line.productId}">
                                <button type="submit" class="link danger" aria-label="Xóa ${fn:escapeXml(line.name)} khỏi giỏ">Xóa sản phẩm</button>
                            </form>
                        </div>
                    </article>
                </c:forEach>
                <form class="shop-clear" action="${pageContext.request.contextPath}/cart" method="post">
                    <input type="hidden" name="csrf" value="${csrf}">
                    <input type="hidden" name="revision" value="${cart.revision}">
                    <input type="hidden" name="action" value="clear">
                    <button type="submit">Xóa toàn bộ giỏ hàng</button>
                </form>
            </div>
            <aside class="panel shop-summary" aria-label="Tóm tắt giỏ hàng">
                <h2>Tóm tắt giỏ hàng</h2>
                <div class="shop-summary-row"><span>Loại sản phẩm</span><strong>${fn:length(cart.lines)}</strong></div>
                <div class="shop-summary-row"><span>Thanh toán</span><strong>Khi nhận hàng</strong></div>
                <div class="shop-summary-row shop-total"><span>Tạm tính</span><strong><fmt:formatNumber value="${cart.total}" maxFractionDigits="0" /> ₫</strong></div>
                <c:choose><c:when test="${cart.checkoutAllowed}"><a class="button" href="${pageContext.request.contextPath}/checkout">Đặt hàng COD →</a></c:when><c:otherwise><p class="hint">Cập nhật các sản phẩm không hợp lệ để tiếp tục.</p></c:otherwise></c:choose>
                <p class="muted">Số lượng từ 1 đến tồn kho hiện tại. Giá và tồn kho được kiểm tra lại khi xác nhận đặt hàng.</p>
            </aside>
        </div>
    </c:otherwise></c:choose>
</section>
</body></html>
