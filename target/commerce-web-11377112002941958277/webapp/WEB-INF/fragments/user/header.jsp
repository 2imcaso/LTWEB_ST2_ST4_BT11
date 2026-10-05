<%@ page pageEncoding="UTF-8" %>
<header class="site-header">
 <div class="topline"><span><b class="live-dot">●</b> Khám phá thế giới video cùng VidStream</span><span>VIDEO PLATFORM · 24133056</span></div>
 <div class="header-row"><a class="brand" href="${ctx}/home"><span class="brand-icon">▶</span> Vid<span>Stream</span></a>
 <p class="header-caption">Không gian của những câu chuyện đáng xem.</p>
 <nav class="account-nav" aria-label="Tài khoản"><c:choose><c:when test="${empty sessionScope.user}"><a href="${ctx}/login">Đăng nhập</a><a class="button" href="${ctx}/register">Đăng ký</a></c:when><c:otherwise><span class="user-name"><c:out value="${sessionScope.user.fullname}"/></span><a href="${ctx}/logout">Đăng xuất</a></c:otherwise></c:choose><c:if test="${sessionScope.user.admin}"><a class="button" href="${ctx}/admin">Trang quản trị</a></c:if></nav></div>
 <nav class="main-nav" aria-label="Điều hướng"><a href="${ctx}/home">Trang chủ</a><a href="${ctx}/home#categories">Khám phá danh mục</a><a href="${ctx}/products" ${shopSection == 'products' ? 'aria-current="page"' : ''}>Sản phẩm</a><a href="${ctx}/cart" ${shopSection == 'cart' || shopSection == 'checkout' ? 'aria-current="page"' : ''}>Giỏ hàng</a><a href="${ctx}/orders" ${shopSection == 'orders' ? 'aria-current="page"' : ''}>Lịch sử đặt hàng</a></nav>
</header>
