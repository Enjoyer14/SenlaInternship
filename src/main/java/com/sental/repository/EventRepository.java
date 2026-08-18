package com.sental.repository;

import com.sental.entity.Event;
import com.sental.entity.Event_;
import com.sental.enums.EventStatus;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EventRepository extends AbstractDao<Event, Long> {
    public List<Event> findByTitleContaining(String keyword) {
        TypedQuery<Event> query = entityManager.createQuery(
                "select e from Event e where lower(e.title) like lower(:keyword)",
                Event.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    public List<Event> findByStatus(EventStatus status) {
        TypedQuery<Event> query = entityManager.createQuery(
                "select e from Event e where e.status = :status",
                Event.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public Optional<Event> findByIdWithAssociationsCriteria(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        root.fetch(Event_.venue, JoinType.LEFT);
        root.fetch(Event_.categories, JoinType.LEFT);
        root.fetch(Event_.organizerIds, JoinType.LEFT);

        query.select(root).distinct(true).where(cb.equal(root.get(Event_.id), id));

        return entityManager.createQuery(query).getResultStream().findFirst();
    }

    public Optional<Event> findByIdWithAssociationsJpql(Long id) {
        TypedQuery<Event> query = entityManager.createQuery(
                "select distinct e from Event e " +
                        "left join fetch e.venue " +
                        "left join fetch e.categories " +
                        "left join fetch e.organizerIds " +
                        "where e.id = :id",
                Event.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    public Optional<Event> findByIdWithAssociationsEntityGraph(Long id) {
        EntityGraph<?> graph = entityManager.getEntityGraph("Event.withAssociations");
        Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
        return Optional.ofNullable(entityManager.find(Event.class, id, hints));
    }
}