package com.sental.repository;

import com.sental.entity.Category;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository extends AbstractDao<Category, Long> {
    public List<Category> findRootCategories() {
        TypedQuery<Category> query = entityManager.createQuery(
                "select c from Category c where c.parent is null",
                Category.class);
        return query.getResultList();
    }
}