package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import KTGK.de03.service.VideoService_24133056;
import KTGK.de03.entity.User_24133056;

@WebServlet("/video")
public class VideoDetailController_24133056 extends BaseController_24133056 {
	private final VideoService_24133056 s = new VideoService_24133056();

	protected void doGet(HttpServletRequest r, HttpServletResponse p) {
		try {
			var v = s.find(r.getParameter("id")).orElse(null);
			if (v == null) {
				p.sendError(404);
				return;
			}
			r.setAttribute("video", v);
			r.setAttribute("likes", s.likes(v.getVideoId()));
			r.setAttribute("shares", s.shares(v.getVideoId()));
			r.setAttribute("product", s.product(v.getVideoId()).filter(product -> product.isActive()).orElse(null));
			var session = r.getSession(false);
			var user = session == null ? null : (User_24133056) session.getAttribute("user");
			if (user != null) {
				var state = CommerceSession_24133056.get(session, user.getUsername());
				r.setAttribute("csrf", state.csrf());
				r.setAttribute("revision", state.cart().getRevision());
			}
			view(r, p, "video-detail");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




