package KTGK.de03.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "Category")
public class Category_24133056 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer categoryId;
	@Column(length = 100)
	private String categoryName;
	@Column(length = 100, unique = true)
	private String categoryCode;
	@Column(length = 500)
	private String images;
	private boolean status;
	@OneToMany(mappedBy = "category")
	private List<Video_24133056> videos = new ArrayList<>();

	public Category_24133056() {
	}

	public Category_24133056(String n, String c) {
		categoryName = n;
		categoryCode = c;
		status = true;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer v) {
		categoryId = v;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String v) {
		categoryName = v;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String v) {
		categoryCode = v;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String v) {
		images = v;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean v) {
		status = v;
	}

	public List<Video_24133056> getVideos() {
		return videos;
	}

	public void setVideos(List<Video_24133056> v) {
		videos = v;
	}
}



