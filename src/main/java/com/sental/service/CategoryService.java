package com.sental.service;

import com.sental.dto.request.CategoryCreateRequest;
import com.sental.dto.response.CategoryResponse;
import com.sental.entity.Category;
import com.sental.exception.EntityNotFound;
import com.sental.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse create(CategoryCreateRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setParentId(request.getParentId());

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Category not found: " + id));
        return toResponse(category);
    }

    public List<CategoryResponse> findAll() {
        List<CategoryResponse> result = new ArrayList<>();
        for (Category category : categoryRepository.findAll()) {
            result.add(toResponse(category));
        }
        return result;
    }

    public void delete(Long id) {
        categoryRepository.delete(id);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentId())
                .build();
    }
}