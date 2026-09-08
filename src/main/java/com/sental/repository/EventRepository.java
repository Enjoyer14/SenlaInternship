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

        CriteriaQuery<Event> query1 = cb.createQuery(Event.class);
        Root<Event> root1 = query1.from(Event.class);
        root1.fetch(Event_.venue, JoinType.LEFT);
        root1.fetch(Event_.categories, JoinType.LEFT);
        query1.select(root1).distinct(true).where(cb.equal(root1.get(Event_.id), id));

        List<Event> results = entityManager.createQuery(query1).getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }

        CriteriaQuery<Event> query2 = cb.createQuery(Event.class);
        Root<Event> root2 = query2.from(Event.class);
        root2.fetch(Event_.organizerIds, JoinType.LEFT);
        query2.select(root2).distinct(true).where(cb.equal(root2.get(Event_.id), id));

        return entityManager.createQuery(query2).getResultStream().findFirst();
    }

    public Optional<Event> findByIdWithAssociationsJpql(Long id) {
        List<Event> events = entityManager.createQuery(
                        "select distinct e from Event e " +
                                "left join fetch e.venue " +
                                "left join fetch e.categories " +
                                "where e.id = :id",
                        Event.class)
                .setParameter("id", id)
                .getResultList();

        if (events.isEmpty()) {
            return Optional.empty();
        }

        return entityManager.createQuery(
                        "select distinct e from Event e " +
                                "left join fetch e.organizerIds " +
                                "where e.id = :id",
                        Event.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    public Optional<Event> findByIdWithAssociationsEntityGraph(Long id) {
        EntityGraph<Event> graph = entityManager.createEntityGraph(Event.class);
        graph.addAttributeNodes("venue", "categories");

        Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
        Event event = entityManager.find(Event.class, id, hints);

        if (event != null) {
            event.getOrganizerIds().size();
        }

        return Optional.ofNullable(event);
    }
}
