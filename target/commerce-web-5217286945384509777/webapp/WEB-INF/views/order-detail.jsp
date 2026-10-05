<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!doctype html><html lang="vi"><head><title>Chi tiết đơn hàng</title><%@ include file="/WEB-INF/fragments/shop/head.jspf" %></head><body>
<section class="shop-page">
    <div class="shop-heading"><div><p class="eyebrow">VIDSTREAM · CHI TIẾT ĐƠN HÀNG</p><h1>Đơn hàng #${order.id}</h1><p class="muted">Đặt lúc <c:out value="${order.createdAtDisplay}" /></p></div><a class="button secondary" href="${pageContext.request.contextPath}/orders">← Lịch sử đặt hàng</a></div>
    <%@ include file="/WEB-INF/fragments/shop/messages.jspf" %>
    <div class="shop-layout">
        <div>
            <section class="panel"><h2>Sản phẩm đã đặt</h2><div class="table-wrap"><table class="shop-detail-table">
                <thead><tr><th>Sản phẩm</th><th>Đơn giá lúc mua</th><th>Số lượng</th><th>Thành tiền</th></tr></thead>
                <tbody><c:forEach var="item" items="${order.items}"><tr>
                    <td><strong><c:out value="${item.productName}" /></strong><small><c:out value="${item.sku}" /></small></td>
                    <td><fmt:formatNumber value="${item.unitPrice}" maxFractionDigits="0" /> ₫</td><td>${item.quantity}</td><td><fmt:formatNumber value="${item.subtotal}" maxFractionDigits="0" /> ₫</td>
                </tr></c:forEach></tbody>
            </table></div></section>
            <section class="panel shop-details"><h2>Thông tin nhận hàng</h2><dl class="detail-list">
                <dt>Người nhận</dt><dd><c:out value="${order.recipient}" /></dd>
                <dt>Điện thoại</dt><dd><c:out value="${order.phone}" /></dd>
                <dt>Địa chỉ</dt><dd><c:out value="${order.address}" /></dd>
                <dt>Ghi chú</dt><dd><c:choose><c:when test="${empty order.note}">Không có ghi chú</c:when><c:otherwise><c:out value="${order.note}" /></c:otherwise></c:choose></dd>
            </dl></section>
        </div>
        <aside class="panel shop-summary" aria-label="Trạng thái và thanh toán"><h2>Thông tin đơn hàng</h2>
            <span class="shop-status shop-status-${order.status.code}"><c:out value="${order.status.label}" /></span>
            <div class="shop-summary-row"><span>Phương thức</span><strong>COD</strong></div>
            <div class="shop-summary-row shop-total"><span>Tổng thanh toán</span><strong><fmt:formatNumber value="${order.total}" maxFractionDigits="0" /> ₫</strong></div>
            <p class="muted">Thanh toán tiền mặt khi nhận hàng. Giá sản phẩm được lưu tại thời điểm bạn đặt đơn.</p>
        </aside>
    </div>
</section>
</body></html>
