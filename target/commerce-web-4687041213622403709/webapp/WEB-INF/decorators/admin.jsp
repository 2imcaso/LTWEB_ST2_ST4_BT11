<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title><% out.write("<sitemesh:write property=\"title\"/>"); %> · VidStream</title>
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;family=Space+Grotesk:wght@600;700&amp;display=swap" rel="stylesheet">
<link href="${ctx}/assets/app.css" rel="stylesheet"><% out.write("<sitemesh:write property=\"head\"/>"); %>
</head><body class="admin-shell">
<a class="skip-link" href="#main-content">Đến nội dung chính</a>
<%@ include file="/WEB-INF/fragments/admin/header.jsp" %>
<aside class="studio-sidebar"><p class="sidebar-label">QUẢN LÝ HỆ THỐNG</p><nav aria-label="Quản trị"><a class="${adminSection=='dashboard'?'selected':''}" href="${ctx}/admin" ${adminSection=='dashboard'?'aria-current="page"':''}><span aria-hidden="true">▦</span> Tổng quan &amp; báo cáo</a><a class="${adminSection=='videos'?'selected':''}" href="${ctx}/admin/videos" ${adminSection=='videos'?'aria-current="page"':''}><span aria-hidden="true">▷</span> Quản lý video</a><a href="${ctx}/home"><span aria-hidden="true">⌂</span> Trang người dùng</a><a href="${ctx}/home#categories"><span aria-hidden="true">▤</span> Khám phá danh mục</a><a href="${ctx}/products"><span aria-hidden="true">▣</span> Cửa hàng</a><a href="${ctx}/cart"><span aria-hidden="true">＋</span> Giỏ hàng</a><a href="${ctx}/orders"><span aria-hidden="true">≡</span> Đơn mua của tôi</a></nav><div class="sidebar-session"><p>Phiên làm việc <span class="online-dot">●</span></p><strong><c:out value="${sessionScope.user.username}"/></strong><a href="${ctx}/logout">Đăng xuất ↗</a></div></aside>
<div class="studio-workspace"><main id="main-content" class="studio-main"><% out.write("<sitemesh:write property=\"body\"/>"); %></main><%@ include file="/WEB-INF/fragments/admin/footer.jsp" %></div>
</body></html>
