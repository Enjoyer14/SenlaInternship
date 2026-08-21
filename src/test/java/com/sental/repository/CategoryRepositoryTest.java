package com.sental.repository;

import com.sental.config.TestPersistenceConfig;
import com.sental.entity.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestPersistenceConfig.class)
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void findRootCategories() {
        Category category = new Category();
        category.setName("Music");
        categoryRepository.save(category);
        entityManager.flush();

        List<Category> roots = categoryRepository.findRootCategories();

        assertTrue(roots.stream().anyMatch(item -> "Music".equals(item.getName())));
    }
}