<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!doctype html><html lang="vi"><head><title>Sản phẩm</title><%@ include file="/WEB-INF/fragments/shop/head.jspf" %></head><body>
<section class="shop-page">
    <div class="shop-heading">
        <div><p class="eyebrow">VIDSTREAM · CỬA HÀNG</p><h1>Video dành cho bạn</h1><p class="muted">Chọn nội dung yêu thích, thêm vào giỏ và hoàn tất đơn hàng.</p></div>
        <div class="shop-actions"><a class="button secondary" href="${pageContext.request.contextPath}/orders">Đơn hàng của tôi</a><a class="button" href="${pageContext.request.contextPath}/cart">Xem giỏ hàng ↗</a></div>
    </div>
    <%@ include file="/WEB-INF/fragments/shop/messages.jspf" %>
    <c:choose><c:when test="${empty catalog.items}">
        <div class="panel shop-empty"><span class="shop-product-mark" aria-hidden="true">▣</span><h2>Chưa có sản phẩm đang bán</h2><p>Cửa hàng chưa có sản phẩm. Bạn có thể tiếp tục khám phá kho video.</p><a class="button secondary" href="${pageContext.request.contextPath}/home">Về trang chủ</a></div>
    </c:when><c:otherwise>
        <div class="shop-grid">
            <c:forEach var="product" items="${catalog.items}">
                <article class="shop-product">
                    <div class="shop-product-top ${not empty product.poster ? 'has-poster' : ''}"><c:choose><c:when test="${not empty product.poster}"><img class="shop-product-poster" src="<c:out value='${product.poster}'/>" alt="<c:out value='${product.name}'/>" loading="lazy"></c:when><c:otherwise><span class="shop-product-mark" aria-hidden="true">▣</span></c:otherwise></c:choose><span class="shop-stock ${product.stock == 0 ? 'empty' : ''}">${product.stock == 0 ? 'Hết lượt bán' : 'Còn lượt mua'} · ${product.stock}</span></div>
                    <div class="shop-product-body">
                        <p class="shop-sku"><c:out value="${product.sku}" /></p>
                        <h2><c:out value="${product.name}" /></h2>
                        <c:if test="${not empty product.videoId}"><a class="shop-video-link" href="${pageContext.request.contextPath}/video?id=${product.videoId}">Xem thông tin video ↗</a></c:if>
                        <p class="shop-price"><fmt:formatNumber value="${product.price}" maxFractionDigits="0" /> ₫</p>
                        <c:choose>
                            <c:when test="${product.stock == 0}"><p class="hint">Sản phẩm hiện đã hết hàng.</p></c:when>
                            <c:when test="${empty sessionScope.user}"><a class="button" href="${pageContext.request.contextPath}/login">＋ Thêm vào giỏ</a></c:when>
                            <c:otherwise>
                                <form class="shop-buy-form" action="${pageContext.request.contextPath}/cart" method="post">
                                    <input type="hidden" name="csrf" value="${csrf}">
                                    <input type="hidden" name="revision" value="${revision}">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <label>Số lượng<input type="number" name="quantity" min="1" max="${product.stock}" step="1" value="1" required></label>
                                    <button type="submit">＋ Thêm vào giỏ</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </article>
            </c:forEach>
        </div>
        <nav class="pagination" aria-label="Phân trang sản phẩm">
            <c:if test="${catalog.hasPrevious}"><a href="?page=${catalog.page - 1}">← Trang trước</a></c:if>
            <span>Trang ${catalog.page} / ${catalog.pages}</span>
            <c:if test="${catalog.hasNext}"><a href="?page=${catalog.page + 1}">Trang sau →</a></c:if>
        </nav>
    </c:otherwise></c:choose>
</section>
</body></html>
