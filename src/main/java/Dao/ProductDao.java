package Dao;

import Model.Product;
import Model.User;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProductDao extends GenericDao<Product> {
    public ProductDao() {
        super();
    }
    public List<Product> findAllByUserIdAndDeletedFalse(long userId) {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product p where p.owner.id = :userId and p.deleted = false", Product.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    public Product findByIdAndUserId(long id, long userId) {
        TypedQuery<Product> query = entityManager.createQuery("select p from Product p where p.id = :id and p.owner.id = :userId", Product.class);
        query.setParameter("id", id);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
    public Product findByName(String name) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.name = :username", Product.class);
        query.setParameter("username", name);
        return query.getResultStream().findFirst().orElse(null);
    }
}
