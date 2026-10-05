package KTGK.de03.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Videos")
public class Video_24133056 {
	@Id
	@Column(length = 50)
	private String videoId;
	@Column(length = 200)
	private String title;
	@Column(length = 500)
	private String poster;
	private int views;
	@Column(length = 500)
	private String description;
	private boolean active;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CategoryId")
	private Category_24133056 category;

	public Video_24133056() {
	}

	public Video_24133056(String i, String t, String p, int v, String d, Category_24133056 c) {
		videoId = i;
		title = t;
		poster = p;
		views = v;
		description = d;
		category = c;
		active = true;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String v) {
		videoId = v;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String v) {
		title = v;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String v) {
		poster = v;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int v) {
		views = v;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String v) {
		description = v;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean v) {
		active = v;
	}

	public Category_24133056 getCategory() {
		return category;
	}

	public void setCategory(Category_24133056 v) {
		category = v;
	}
}



