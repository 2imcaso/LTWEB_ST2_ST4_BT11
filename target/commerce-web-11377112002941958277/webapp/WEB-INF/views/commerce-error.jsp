<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html lang="vi"><head><title>Kiểm tra yêu cầu</title><%@ include file="/WEB-INF/fragments/shop/head.jspf" %></head><body>
<section class="shop-page shop-error-page">
    <p class="eyebrow">VIDSTREAM · KIỂM TRA YÊU CẦU</p>
    <div class="panel shop-empty"><h1>Chưa thể thực hiện thao tác</h1><p class="error" role="alert"><c:out value="${error}" /></p>
        <div class="shop-actions"><a class="button" href="${pageContext.request.contextPath}/cart">Kiểm tra giỏ hàng</a><a class="button secondary" href="${pageContext.request.contextPath}/orders">Lịch sử đặt hàng</a><a class="button secondary" href="${pageContext.request.contextPath}/login">Đăng nhập lại</a></div>
    </div>
</section>
</body></html>
