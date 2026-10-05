package KTGK.de03.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Shares")
public class Share_24133056 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer shareId;
	@Column(length = 50)
	private String emails;
	private LocalDate sharedDate;
	@ManyToOne
	@JoinColumn(name = "Username")
	private User_24133056 user;
	@ManyToOne
	@JoinColumn(name = "VideoId")
	private Video_24133056 video;

	public Integer getShareId() {
		return shareId;
	}

	public String getEmails() {
		return emails;
	}

	public void setEmails(String v) {
		emails = v;
	}

	public LocalDate getSharedDate() {
		return sharedDate;
	}

	public void setSharedDate(LocalDate v) {
		sharedDate = v;
	}

	public User_24133056 getUser() {
		return user;
	}

	public void setUser(User_24133056 v) {
		user = v;
	}

	public Video_24133056 getVideo() {
		return video;
	}

	public void setVideo(Video_24133056 v) {
		video = v;
	}
}



