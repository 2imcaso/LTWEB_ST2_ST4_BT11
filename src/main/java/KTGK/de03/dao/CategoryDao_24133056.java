package KTGK.de03.dao;

import KTGK.de03.entity.Category_24133056;
import java.util.*;

public interface CategoryDao_24133056 {
	List<Category_24133056> findAll();

	Optional<Category_24133056> find(int id);

	void save(Category_24133056 c);
}



