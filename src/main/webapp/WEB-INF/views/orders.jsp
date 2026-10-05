<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!doctype html><html lang="vi"><head><title>Lịch sử đặt hàng</title><%@ include file="/WEB-INF/fragments/shop/head.jspf" %></head><body>
<section class="shop-page">
    <div class="shop-heading"><div><p class="eyebrow">VIDSTREAM · ĐƠN HÀNG</p><h1>Lịch sử đặt hàng</h1><p class="muted">Theo dõi đơn mua của bạn và xem trạng thái hiện tại.</p></div><a class="button secondary" href="${pageContext.request.contextPath}/products">Tiếp tục mua hàng ↗</a></div>
    <%@ include file="/WEB-INF/fragments/shop/messages.jspf" %>
    <form class="shop-filter" action="${pageContext.request.contextPath}/orders" method="get">
        <label>Trạng thái đơn hàng<select name="status"><option value="">Tất cả trạng thái</option><c:forEach var="status" items="${statuses}"><option value="${status.code}" ${selectedStatus == status.code ? 'selected' : ''}><c:out value="${status.label}" /></option></c:forEach></select></label>
        <button type="submit">Lọc đơn hàng</button>
    </form>
    <c:choose><c:when test="${empty history.items}">
        <div class="panel shop-empty"><span class="shop-product-mark" aria-hidden="true">≡</span><h2>Không có đơn hàng phù hợp</h2><p>Thử chọn trạng thái khác hoặc khám phá sản phẩm để tạo đơn hàng đầu tiên.</p><a class="button secondary" href="${pageContext.request.contextPath}/orders">Xem tất cả đơn hàng</a></div>
    </c:when><c:otherwise>
        <div class="panel shop-orders"><div class="table-wrap"><table>
            <thead><tr><th>Mã đơn</th><th>Ngày đặt</th><th>Trạng thái</th><th>Thanh toán</th><th>Tổng tiền</th><th>Chi tiết</th></tr></thead>
            <tbody><c:forEach var="order" items="${history.items}"><tr>
                <td data-label="Mã đơn"><strong>#${order.id}</strong></td><td data-label="Ngày đặt"><c:out value="${order.createdAtDisplay}" /></td>
                <td data-label="Trạng thái"><span class="shop-status shop-status-${order.status.code}"><c:out value="${order.status.label}" /></span></td>
                <td data-label="Thanh toán">COD</td><td data-label="Tổng tiền"><strong><fmt:formatNumber value="${order.total}" maxFractionDigits="0" /> ₫</strong></td>
                <td data-label="Chi tiết"><a href="${pageContext.request.contextPath}/orders/detail?id=${order.id}">Xem đơn ↗</a></td>
            </tr></c:forEach></tbody>
        </table></div></div>
    </c:otherwise></c:choose>
    <nav class="pagination" aria-label="Phân trang đơn hàng">
        <c:if test="${history.hasPrevious}"><a href="?page=${history.page - 1}&amp;status=${selectedStatus}">← Trang trước</a></c:if>
        <span>Trang ${history.page} / ${history.pages}</span>
        <c:if test="${history.hasNext}"><a href="?page=${history.page + 1}&amp;status=${selectedStatus}">Trang sau →</a></c:if>
    </nav>
</section>
</body></html>
