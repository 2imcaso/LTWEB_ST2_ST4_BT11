package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController_24133056 extends HttpServlet {
	protected void doGet(HttpServletRequest r, HttpServletResponse p) throws IOException {
		r.getSession().invalidate();
		p.sendRedirect(r.getContextPath() + "/login");
	}
}




