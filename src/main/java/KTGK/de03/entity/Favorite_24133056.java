package KTGK.de03.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Favorites", uniqueConstraints = @UniqueConstraint(columnNames = { "VideoId", "Username" }))
public class Favorite_24133056 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteId;
	private LocalDate likedDate;
	@ManyToOne
	@JoinColumn(name = "VideoId")
	private Video_24133056 video;
	@ManyToOne
	@JoinColumn(name = "Username")
	private User_24133056 user;

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public LocalDate getLikedDate() {
		return likedDate;
	}

	public void setLikedDate(LocalDate v) {
		likedDate = v;
	}

	public Video_24133056 getVideo() {
		return video;
	}

	public void setVideo(Video_24133056 v) {
		video = v;
	}

	public User_24133056 getUser() {
		return user;
	}

	public void setUser(User_24133056 v) {
		user = v;
	}
}



