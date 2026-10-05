<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="jakarta.tags.core" %><!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Đăng ký</title></head><body>
<section class="auth-box"><p class="eyebrow">VIDSTREAM · TÀI KHOẢN</p>
	<h1>Đăng ký tài khoản</h1>
	<c:if test="${not empty error}">
		<p class="error">${error}</p>
	</c:if>
	<form method="post">
		<label>Tên đăng nhập<input name="username" maxlength="50"
			required></label><label>Họ và tên<input name="fullname"
			maxlength="50" required></label><label>Email<input
			type="email" name="email" maxlength="150" required></label><label>Số
			điện thoại<input name="phone" maxlength="15">
		</label><label>Mật khẩu<input type="password" name="password"
			minlength="6" required></label>
		<button>Đăng ký và nhận OTP</button>
	</form>
</section>
</body></html>

