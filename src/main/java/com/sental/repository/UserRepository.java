package com.sental.repository;

import com.sental.entity.User;
import com.sental.entity.User_;
import com.sental.enums.UserRole;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends AbstractDao<User, Long> {
    public Optional<User> findByUsername(String username) {
        List<User> users = entityManager.createQuery(
                        "select u from User u where u.username = :username",
                        User.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList();

        return users.stream().findFirst();
    }

    public List<User> findByRoleAndEmail(UserRole role, String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.select(root).where(cb.and(
                cb.equal(root.get(User_.role), role),
                cb.equal(root.get(User_.email), email)
        ));

        return entityManager.createQuery(query).getResultList();
    }
}