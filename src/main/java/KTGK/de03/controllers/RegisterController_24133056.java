package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import KTGK.de03.entity.User_24133056;
import KTGK.de03.service.AuthService_24133056;
import KTGK.de03.service.MailService_24133056;
import java.security.SecureRandom;

@WebServlet("/register")
public class RegisterController_24133056 extends BaseController_24133056 {
	private final AuthService_24133056 s = new AuthService_24133056();
	private final MailService_24133056 mailService = new MailService_24133056();

	protected void doGet(HttpServletRequest r, HttpServletResponse p) {
		try {
			view(r, p, "register");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void doPost(HttpServletRequest r, HttpServletResponse p) {
		try {
			String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
			User_24133056 u = new User_24133056(r.getParameter("username"), r.getParameter("password"), r.getParameter("fullname"),
					r.getParameter("email"), false, false);
			u.setPhone(r.getParameter("phone"));
			s.register(u);
			HttpSession x = r.getSession();
			x.setAttribute("otp", otp);
			x.setAttribute("otpUser", u.getUsername());
			x.setAttribute("otpExpires", System.currentTimeMillis() + 300000);
			mailService.sendOtp(u.getEmail(), otp);
			p.sendRedirect(r.getContextPath() + "/verify-otp");
		} catch (Exception e) {
			r.setAttribute("error", e.getMessage());
			try {
				view(r, p, "register");
			} catch (Exception x) {
				throw new RuntimeException(x);
			}
		}
	}
}




