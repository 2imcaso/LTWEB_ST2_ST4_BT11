package KTGK.de03.service;

import KTGK.de03.dao.*;
import KTGK.de03.dao.impl.*;
import KTGK.de03.entity.*;
import java.util.*;

public class VideoService_24133056 {
	private final VideoDao_24133056 v = new VideoDaoImpl_24133056();
	private final CategoryDao_24133056 c = new CategoryDaoImpl_24133056();
	private final CommerceService_24133056 commerce = new CommerceService_24133056();

	public List<Video_24133056> page(int p, int s) {
		return v.page(Math.max(1, p), s);
	}

	public long count() {
		return v.count();
	}

	public Optional<Video_24133056> find(String id) {
		return v.find(id);
	}

	public List<Category_24133056> categories() {
		return c.findAll();
	}

	public Map<Category_24133056, List<Video_24133056>> home(int p) {
		Map<Category_24133056, List<Video_24133056>> m = new LinkedHashMap<>();
		for (Category_24133056 x : c.findAll())
			m.put(x, v.pageByCategory(x.getCategoryId(), p, 3));
		return m;
	}

	public long countByCategory(int id) {
		return v.countByCategory(id);
	}

	public long likes(String id) {
		return v.likes(id);
	}

	public long shares(String id) {
		return v.shares(id);
	}

	public void save(String id, String title, String poster, int views, String description, boolean active,
			int categoryId) {
		Category_24133056 x = c.find(categoryId).orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
		Video_24133056 y = v.find(id).orElseGet(Video_24133056::new);
		y.setVideoId(id);
		y.setTitle(title);
		y.setPoster(poster);
		y.setViews(views);
		y.setDescription(description);
		y.setActive(active);
		y.setCategory(x);
		v.save(y);
	}

	public void save(String id, String title, String poster, int views, String description, boolean active,
			int categoryId, java.math.BigDecimal price, int stock, boolean sellable) {
		save(id, title, poster, views, description, active, categoryId);
		commerce.saveVideoProduct(id, price, stock, sellable && active);
	}

	public void delete(String id) {
		commerce.deleteVideoProduct(id);
		v.delete(id);
	}

	public Optional<Product_24133056> product(String videoId) {
		return commerce.productForVideo(videoId);
	}
}



