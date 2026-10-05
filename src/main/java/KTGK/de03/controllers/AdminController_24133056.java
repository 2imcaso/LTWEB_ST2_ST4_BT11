package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.math.BigDecimal;
import KTGK.de03.service.VideoService_24133056;

@WebServlet("/admin/videos")
public class AdminController_24133056 extends BaseController_24133056 {
	private final VideoService_24133056 s = new VideoService_24133056();

	protected void doGet(HttpServletRequest r, HttpServletResponse p) {
		try {
			r.setAttribute("adminSection", "videos");
			int page = page(r);
			r.setAttribute("videos", s.page(page, 6));
			r.setAttribute("categories", s.categories());
			r.setAttribute("page", page);
			r.setAttribute("totalPages", Math.max(1, (s.count() + 5) / 6));
			if (r.getParameter("edit") != null)
			{
				String id = r.getParameter("edit");
				r.setAttribute("editing", s.find(id).orElse(null));
				r.setAttribute("editingProduct", s.product(id).orElse(null));
			}
			view(r, p, "admin");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void doPost(HttpServletRequest r, HttpServletResponse p) {
		try {
			String action = r.getParameter("action");
			if ("delete".equals(action))
				s.delete(r.getParameter("id"));
			else
				s.save(r.getParameter("id"), r.getParameter("title"), r.getParameter("poster"),
						Integer.parseInt(r.getParameter("views")), r.getParameter("description"),
						r.getParameter("active") != null, Integer.parseInt(r.getParameter("categoryId")),
						new BigDecimal(r.getParameter("price")), Integer.parseInt(r.getParameter("stock")),
						r.getParameter("sellable") != null);
			p.sendRedirect(r.getContextPath() + "/admin/videos");
		} catch (Exception e) {
			r.setAttribute("error", e.getMessage());
			doGet(r, p);
		}
	}
}




