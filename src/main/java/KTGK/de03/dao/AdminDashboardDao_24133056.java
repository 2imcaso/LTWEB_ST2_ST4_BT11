package KTGK.de03.dao;

import java.util.LinkedHashMap;
import java.util.Map;
import KTGK.de03.entity.Video_24133056;

public class AdminDashboardDao_24133056 {
    public Map<String, Object> load() {
        try (var em = JpaUtil_24133056.em()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("users", em.createQuery("select count(u) from User_24133056 u", Long.class).getSingleResult());
            result.put("videos", em.createQuery("select count(v) from Video_24133056 v", Long.class).getSingleResult());
            result.put("published", em.createQuery("select count(v) from Video_24133056 v where v.active=true", Long.class).getSingleResult());
            result.put("views", em.createQuery("select coalesce(sum(v.views),0) from Video_24133056 v", Long.class).getSingleResult());
            result.put("likes", em.createQuery("select count(f) from Favorite_24133056 f", Long.class).getSingleResult());
            result.put("shares", em.createQuery("select count(s) from Share_24133056 s", Long.class).getSingleResult());
            result.put("categories", em.createQuery("select c.categoryName, count(v), coalesce(sum(v.views),0) from Category_24133056 c left join c.videos v group by c.categoryId, c.categoryName order by c.categoryId", Object[].class).getResultList());
            result.put("topVideos", em.createQuery("from Video_24133056 v left join fetch v.category order by v.views desc, v.videoId", Video_24133056.class).setMaxResults(5).getResultList());
            return result;
        }
    }
}
