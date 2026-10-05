<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="jakarta.tags.core" %><!doctype html><html lang="vi"><head><meta charset="UTF-8"><title>Xác thực OTP</title></head><body>
<section class="auth-box"><p class="eyebrow">VIDSTREAM · TÀI KHOẢN</p>
	<h1>Xác thực OTP</h1>
	<p>Mã OTP đã được gửi tới email đăng ký. Mã có hiệu lực trong 5 phút.</p>
	<c:if test="${not empty error}">
		<p class="error">${error}</p>
	</c:if>
	<form method="post">
		<label>Mã OTP<input name="otp" pattern="[0-9]{6}"
			maxlength="6" required autofocus></label>
		<button>Kích hoạt tài khoản</button>
	</form>
</section>
</body></html>

