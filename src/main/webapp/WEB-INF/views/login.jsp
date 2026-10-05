<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="jakarta.tags.core" %><!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Đăng nhập</title></head><body>
<section class="auth-box"><p class="eyebrow">VIDSTREAM · TÀI KHOẢN</p>
	<h1>Đăng nhập</h1>
	<c:if test="${param.activated=='1'}">
		<p class="success">Kích hoạt thành công. Bạn có thể đăng nhập.</p>
	</c:if>
	<c:if test="${not empty error}">
		<p class="error">${error}</p>
	</c:if>
	<form method="post">
		<label>Tên đăng nhập<input name="username" required autofocus></label><label>Mật
			khẩu<input type="password" name="password" required>
		</label>
		<button>Đăng nhập</button>
	</form>
	<p>
		Chưa có tài khoản? <a
			href="${pageContext.request.contextPath}/register">Đăng ký</a>
	</p>
	<p class="hint">Tài khoản mẫu: admin / 123456</p>
</section>
</body></html>

