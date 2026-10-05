package KTGK.de03.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import KTGK.de03.entity.*;

/** Uses the caller's EntityManager so stock and order writes share one transaction. */
public final class CommerceDao_24133056 {
    public Optional<User_24133056> user(EntityManager em, String username, boolean lock) {
        return Optional.ofNullable(lock
                ? em.find(User_24133056.class, username, LockModeType.PESSIMISTIC_WRITE)
                : em.find(User_24133056.class, username));
    }

    public Product_24133056 product(EntityManager em, long id, boolean lock) {
        return lock ? em.find(Product_24133056.class, id, LockModeType.PESSIMISTIC_WRITE)
                    : em.find(Product_24133056.class, id);
    }

    public long productCount(EntityManager em) {
        return em.createQuery("select count(p) from Product_24133056 p where p.active=true", Long.class)
                .getSingleResult();
    }

    public List<Product_24133056> products(EntityManager em, int offset, int size) {
        return em.createQuery("select p from Product_24133056 p left join fetch p.video where p.active=true order by p.id", Product_24133056.class)
                .setFirstResult(offset).setMaxResults(size).getResultList();
    }

    public Optional<Product_24133056> productByVideo(EntityManager em, String videoId) {
        return em.createQuery("select p from Product_24133056 p join fetch p.video v where v.videoId=:videoId",
                Product_24133056.class).setParameter("videoId", videoId).getResultStream().findFirst();
    }

    public List<Product_24133056> productsByVideo(EntityManager em, java.util.Collection<String> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) return List.of();
        return em.createQuery("select p from Product_24133056 p join fetch p.video v "
                + "where p.active=true and v.active=true and v.videoId in :ids", Product_24133056.class)
                .setParameter("ids", videoIds).getResultList();
    }

    public Optional<ShopOrder_24133056> byToken(EntityManager em, String username, String token) {
        return em.createQuery("from ShopOrder_24133056 o where o.customer.username=:owner and o.checkoutToken=:token",
                ShopOrder_24133056.class).setParameter("owner", username).setParameter("token", token)
                .getResultStream().findFirst();
    }

    public Optional<ShopOrder_24133056> order(EntityManager em, String username, long id) {
        return em.createQuery("select distinct o from ShopOrder_24133056 o left join fetch o.items "
                + "where o.customer.username=:owner and o.id=:id", ShopOrder_24133056.class)
                .setParameter("owner", username).setParameter("id", id).getResultStream().findFirst();
    }

    public long orderCount(EntityManager em, String username, OrderStatus_24133056 status) {
        var query = em.createQuery("select count(o) from ShopOrder_24133056 o where o.customer.username=:owner"
                + (status == null ? "" : " and o.status=:status"), Long.class).setParameter("owner", username);
        if (status != null) query.setParameter("status", status);
        return query.getSingleResult();
    }

    public List<ShopOrder_24133056> orders(EntityManager em, String username, OrderStatus_24133056 status,
            int offset, int size) {
        var query = em.createQuery("from ShopOrder_24133056 o where o.customer.username=:owner"
                + (status == null ? "" : " and o.status=:status") + " order by o.id desc", ShopOrder_24133056.class)
                .setParameter("owner", username).setFirstResult(offset).setMaxResults(size);
        if (status != null) query.setParameter("status", status);
        return query.getResultList();
    }
}
