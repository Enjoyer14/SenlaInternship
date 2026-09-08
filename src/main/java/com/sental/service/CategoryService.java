package com.sental.service;

import com.sental.dto.request.create.CategoryCreateRequest;
import com.sental.dto.request.update.CategoryUpdateRequest;
import com.sental.dto.response.CategoryResponse;
import com.sental.entity.Category;
import com.sental.exception.EntityNotFound;
import com.sental.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId()).orElseThrow(() -> new EntityNotFound("Category not found: " + request.getParentId()));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Category not found: " + id));
        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        List<CategoryResponse> result = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            result.add(toResponse(category));
        }
        return result;
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Category not found: " + id));
        category.setName(request.getName());
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId()).orElseThrow(() -> new EntityNotFound("Category parent not found: " + request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Category not found: " + id));
        categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() == null ? null : category.getParent().getId())
                .build();
    }
}