package KTGK.de03.dao;

import KTGK.de03.entity.Video_24133056;
import java.util.*;

public interface VideoDao_24133056 {
	Optional<Video_24133056> find(String id);

	List<Video_24133056> page(int p, int s);

	List<Video_24133056> pageByCategory(int id, int p, int s);

	long count();

	long countByCategory(int id);

	void save(Video_24133056 v);

	void delete(String id);

	long likes(String id);

	long shares(String id);
}



