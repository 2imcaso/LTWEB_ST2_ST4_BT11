package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import KTGK.de03.service.VideoService_24133056;
import KTGK.de03.service.CommerceService_24133056;
import KTGK.de03.entity.User_24133056;
import java.util.*;

@WebServlet("/home")
public class HomeController_24133056 extends BaseController_24133056 {
	private final VideoService_24133056 s = new VideoService_24133056();
	private final CommerceService_24133056 commerce = new CommerceService_24133056();

	protected void doGet(HttpServletRequest r, HttpServletResponse p) {
		try {
			int page = page(r);
			var groups = s.home(page);
			r.setAttribute("groups", groups);
			Set<String> videoIds = new HashSet<>();
			groups.values().forEach(videos -> videos.forEach(video -> videoIds.add(video.getVideoId())));
			r.setAttribute("productsByVideo", commerce.productsForVideos(videoIds));
			var session = r.getSession(false);
			var user = session == null ? null : (User_24133056) session.getAttribute("user");
			if (user != null) {
				var state = CommerceSession_24133056.get(session, user.getUsername());
				r.setAttribute("csrf", state.csrf());
				r.setAttribute("revision", state.cart().getRevision());
			}
			Map<Integer, Long> counts = new HashMap<>();
			long max = 0;
			for (var c : s.categories()) {
				long n = s.countByCategory(c.getCategoryId());
				counts.put(c.getCategoryId(), n);
				max = Math.max(max, n);
			}
			r.setAttribute("counts", counts);
			r.setAttribute("page", page);
			r.setAttribute("totalPages", Math.max(1, (max + 2) / 3));
			view(r, p, "home");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




