package com.sental.repository;

import com.sental.entity.Venue;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VenueRepository extends AbstractDao<Venue, Long> {

    public List<Venue> findByCity(String city) {
        TypedQuery<Venue> query = entityManager.createQuery(
                "select v from Venue v where v.city = :city",
                Venue.class);
        query.setParameter("city", city);
        return query.getResultList();
    }
}