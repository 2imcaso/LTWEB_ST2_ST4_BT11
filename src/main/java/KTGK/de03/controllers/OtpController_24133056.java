package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import KTGK.de03.service.AuthService_24133056;

@WebServlet("/verify-otp")
public class OtpController_24133056 extends BaseController_24133056 {
	private final AuthService_24133056 s = new AuthService_24133056();

	protected void doGet(HttpServletRequest r, HttpServletResponse p) {
		try {
			view(r, p, "verify-otp");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void doPost(HttpServletRequest r, HttpServletResponse p) {
		try {
			HttpSession x = r.getSession();
			String expected = (String) x.getAttribute("otp");
			Long expires = (Long) x.getAttribute("otpExpires");
			if (expected == null || expires == null || System.currentTimeMillis() > expires
					|| !expected.equals(r.getParameter("otp"))) {
				r.setAttribute("error", "OTP không đúng hoặc đã hết hạn");
				view(r, p, "verify-otp");
				return;
			}
			s.activate((String) x.getAttribute("otpUser"));
			x.removeAttribute("otp");
			x.removeAttribute("otpUser");
			x.removeAttribute("demoOtp");
			p.sendRedirect(r.getContextPath() + "/login?activated=1");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




