package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import KTGK.de03.service.AuthService_24133056;

@WebServlet("/login")
public class LoginController_24133056 extends BaseController_24133056 {
	private final AuthService_24133056 s = new AuthService_24133056();

	protected void doGet(HttpServletRequest r, HttpServletResponse p) {
		try {
			view(r, p, "login");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void doPost(HttpServletRequest r, HttpServletResponse p) {
		var u = s.login(r.getParameter("username"), r.getParameter("password"));
		try {
			if (u.isEmpty()) {
				r.setAttribute("error", "Sai tài khoản, mật khẩu hoặc tài khoản chưa kích hoạt");
				view(r, p, "login");
				return;
			}
			r.getSession().setAttribute("user", u.get());
			p.sendRedirect(r.getContextPath() + (u.get().isAdmin() ? "/admin" : "/home"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




