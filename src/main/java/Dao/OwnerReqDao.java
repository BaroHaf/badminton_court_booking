package Dao;

import Model.OwnerReq;
import Model.User;
import jakarta.persistence.TypedQuery;

public class OwnerReqDao  extends GenericDao<OwnerReq>{
    public OwnerReqDao() {
        super();
    }
    public OwnerReq findByUsername(String username) {
        TypedQuery<OwnerReq> query = entityManager.createQuery("SELECT u FROM OwnerReq u WHERE u.user_name = :username", OwnerReq.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst().orElse(null);
    }
    public OwnerReq findByToken(String token){
        TypedQuery<OwnerReq> query = entityManager.createQuery("SELECT u FROM OwnerReq u WHERE u.token = :token", OwnerReq.class);
        query.setParameter("token", token);
        return query.getResultStream().findFirst().orElse(null);
    }
}

