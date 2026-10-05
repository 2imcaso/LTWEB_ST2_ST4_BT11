package KTGK.de03.controllers;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import KTGK.de03.entity.User_24133056;
import java.io.IOException;

@WebFilter({"/admin", "/admin/*"})
public class AdminFilter_24133056 implements Filter {
	public void doFilter(ServletRequest a, ServletResponse b, FilterChain c) throws IOException, ServletException {
		HttpServletRequest r = (HttpServletRequest) a;
		HttpServletResponse p = (HttpServletResponse) b;
		User_24133056 u = (User_24133056) r.getSession().getAttribute("user");
		if (u == null || !u.isAdmin()) {
			p.sendRedirect(r.getContextPath() + "/login");
			return;
		}
		c.doFilter(a, b);
	}
}




