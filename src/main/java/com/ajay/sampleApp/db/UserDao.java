package com.ajay.sampleApp.db;

import com.ajay.sampleApp.core.logging.Loggable;
import com.ajay.sampleApp.db.entities.UserEntity;
import com.google.inject.Inject;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.math.BigInteger;
import java.util.List;

@Loggable
public class UserDao extends AbstractDAO<UserEntity> {

    @Inject
    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<UserEntity> listAll() {
        CriteriaBuilder builder = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserEntity> criteria = builder.createQuery(UserEntity.class);
        Root<UserEntity> root = criteria.from(UserEntity.class);
        criteria.select(root);
        return list(criteria);
    }

    public UserEntity findById(BigInteger id) {
        return get(id);
    }

    public UserEntity create(UserEntity userEntity) {
        return persist(userEntity);
    }

    public UserEntity update(UserEntity userEntity) {
        currentSession().merge(userEntity);
        return get(userEntity.getId());
    }

    public void delete(UserEntity userEntity) {
        currentSession().delete(userEntity);
    }
}
