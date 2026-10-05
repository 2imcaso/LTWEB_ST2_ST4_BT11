package KTGK.de03.controllers;

import jakarta.persistence.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import KTGK.de03.dao.JpaUtil_24133056;
import KTGK.de03.entity.*;
import KTGK.de03.service.PasswordUtil_24133056;
import java.math.BigDecimal;
import java.util.List;

@WebListener
public class AppListener_24133056 implements ServletContextListener {
	public void contextInitialized(ServletContextEvent e) {
		EntityManager m = JpaUtil_24133056.em();
		EntityTransaction t = m.getTransaction();
		try {
			t.begin();
			if (m.find(User_24133056.class, "admin") == null)
				m.persist(new User_24133056("admin", PasswordUtil_24133056.hash("123456"), "Quản trị viên", "admin@24133056.local", true,
						true));
			Long n = m.createQuery("select count(c) from Category_24133056 c", Long.class).getSingleResult();
			if (n == 0) {
				String[] names = { "Lập trình", "Âm nhạc", "Giáo dục" };
				for (int k = 0; k < 3; k++) {
					Category_24133056 c = new Category_24133056(names[k], "CAT" + (k + 1));
					m.persist(c);
					for (int i = 1; i <= 7; i++) {
						String id = "V" + (k + 1) + String.format("%02d", i);
						m.persist(new Video_24133056(id, names[k] + " - Video " + i,
								"https://picsum.photos/seed/" + id + "/640/360", 100 * i, "Video minh họa cho đề số 03",
								c));
					}
				}
			}
			seedSellableVideos(m);
			t.commit();
		} catch (RuntimeException x) {
			if (t.isActive())
				t.rollback();
			throw x;
		} finally {
			m.close();
		}
	}

	private void seedSellableVideos(EntityManager m) {
		List<Category_24133056> categories = m.createQuery(
				"from Category_24133056 c order by c.categoryId", Category_24133056.class)
				.setMaxResults(3).getResultList();
		if (categories.isEmpty()) return;
		String[] ids = { "SALE001", "SALE002", "SALE003", "SALE004", "SALE005", "SALE006",
				"SALE007", "SALE008", "SALE009", "SALE010", "SALE011", "SALE012" };
		String[] titles = { "Java Web thực chiến: Xây dựng ứng dụng hoàn chỉnh",
				"Thiết kế giao diện video hiện đại", "JPA & Hibernate từ cơ bản đến dự án",
				"Spring MVC nền tảng cho người mới", "REST API Java và kiểm thử Postman",
				"MySQL thực hành: Thiết kế cơ sở dữ liệu", "Git & GitHub cho dự án nhóm",
				"JavaScript hiện đại qua dự án thực tế", "HTML CSS Responsive chuyên sâu",
				"Kỹ năng dựng video ngắn chuyên nghiệp", "Âm thanh và nhạc nền cho sáng tạo nội dung",
				"Xây dựng portfolio công nghệ nổi bật" };
		String[] descriptions = {
				"Khóa video thực hành Servlet, JSP và mô hình nhiều tầng qua một dự án hoàn chỉnh.",
				"Bộ video hướng dẫn tạo trải nghiệm xem nội dung đẹp, rõ ràng và tương thích di động.",
				"Làm chủ ánh xạ entity, truy vấn, transaction và các lỗi thường gặp khi dùng Hibernate.",
				"Nắm vững controller, model, view và quy trình xây dựng ứng dụng Spring MVC.",
				"Thiết kế REST API rõ ràng, xử lý lỗi và kiểm thử toàn bộ endpoint bằng Postman.",
				"Thực hành chuẩn hóa dữ liệu, quan hệ bảng, chỉ mục và truy vấn tối ưu trong MySQL.",
				"Quản lý phiên bản, branch, pull request và phối hợp hiệu quả trong nhóm phát triển.",
				"Học JavaScript ES6+, xử lý DOM và xây dựng một ứng dụng tương tác hoàn chỉnh.",
				"Tạo giao diện thích ứng tốt trên điện thoại, máy tính bảng và máy tính để bàn.",
				"Quy trình lên ý tưởng, quay, dựng và hoàn thiện video ngắn thu hút người xem.",
				"Cách lựa chọn, xử lý âm thanh và phối nhạc nền phù hợp cho từng loại video.",
				"Xây dựng portfolio cá nhân rõ ràng, chuyên nghiệp và thuyết phục nhà tuyển dụng." };
		long[] prices = { 149000, 99000, 129000, 119000, 139000, 89000,
				79000, 109000, 99000, 69000, 59000, 79000 };
		for (int i = 0; i < ids.length; i++) {
			Video_24133056 video = m.find(Video_24133056.class, ids[i]);
			if (video == null) {
				video = new Video_24133056(ids[i], titles[i],
						"https://picsum.photos/seed/vidstream-" + ids[i] + "/960/540",
						2100 - i * 95, descriptions[i], categories.get(i % categories.size()));
				m.persist(video);
			}
			ensureProduct(m, video, BigDecimal.valueOf(prices[i]));
		}

		// Mọi video trong thư viện đều có mặt hàng tương ứng. Giá mặc định chỉ
		// được dùng khi video chưa từng được cấu hình bán, không ghi đè giá cũ.
		List<Video_24133056> allVideos = m.createQuery(
				"from Video_24133056 v order by v.videoId", Video_24133056.class).getResultList();
		int index = 0;
		for (Video_24133056 video : allVideos) {
			long defaultPrice = 49000L + (index++ % 6) * 10000L;
			ensureProduct(m, video, BigDecimal.valueOf(defaultPrice));
		}
	}

	private void ensureProduct(EntityManager m, Video_24133056 video, BigDecimal defaultPrice) {
		String sku = "VIDEO-" + video.getVideoId();
		List<Product_24133056> products = m.createQuery(
				"from Product_24133056 p where p.video.videoId=:id or p.sku=:sku", Product_24133056.class)
				.setParameter("id", video.getVideoId()).setParameter("sku", sku).setMaxResults(1).getResultList();
		if (products.isEmpty()) {
			Product_24133056 product = new Product_24133056(video, defaultPrice, 100);
			product.updateFromVideo(video, defaultPrice, 100, video.isActive());
			m.persist(product);
		} else if (products.get(0).getVideo() == null) {
			Product_24133056 product = products.get(0);
			product.updateFromVideo(video, product.getPrice(), product.getStock(), product.isActive());
		}
	}

	public void contextDestroyed(ServletContextEvent e) {
		JpaUtil_24133056.close();
	}
}




