package com.sental.repository;

import com.sental.entity.Category;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CategoryRepository {

    private final Map<Long, Category> store = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Category> findAll() {
        return new ArrayList<>(store.values());
    }

    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(id.getAndIncrement());
        }
        store.put(category.getId(), category);
        return category;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}