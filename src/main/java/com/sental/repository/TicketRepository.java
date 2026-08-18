package com.sental.repository;

import com.sental.entity.Ticket;
import com.sental.entity.Ticket_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketRepository extends AbstractDao<Ticket, Long> {

    public List<Ticket> findByUserId(Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Ticket> query = cb.createQuery(Ticket.class);
        Root<Ticket> root = query.from(Ticket.class);

        query.select(root).where(cb.equal(root.get(Ticket_.userId), userId));

        return entityManager.createQuery(query).getResultList();
    }
}