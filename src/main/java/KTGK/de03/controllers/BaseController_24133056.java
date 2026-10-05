package KTGK.de03.controllers;

import jakarta.servlet.http.*;

public abstract class BaseController_24133056 extends HttpServlet {
	protected int page(HttpServletRequest r) {
		try {
			return Math.max(1, Integer.parseInt(r.getParameter("page")));
		} catch (Exception e) {
			return 1;
		}
	}

	protected void view(HttpServletRequest r, HttpServletResponse s, String jsp) throws Exception {
		r.getRequestDispatcher("/WEB-INF/views/" + jsp + ".jsp").forward(r, s);
	}
}




