<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title><% out.write("<sitemesh:write property=\"title\"/>"); %> · VidStream</title>
<link rel="preconnect" href="https://fonts.googleapis.com"><link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&amp;family=Space+Grotesk:wght@600;700&amp;display=swap" rel="stylesheet">
<link href="${ctx}/assets/app.css" rel="stylesheet"><% out.write("<sitemesh:write property=\"head\"/>"); %>
</head><body>
<a class="skip-link" href="#main-content">Đến nội dung chính</a>
<%@ include file="/WEB-INF/fragments/user/header.jsp" %>
<main id="main-content" class="container"><% out.write("<sitemesh:write property=\"body\"/>"); %></main>
<%@ include file="/WEB-INF/fragments/user/footer.jsp" %>
</body></html>
