package com.sental.controller;

import com.sental.exception.SerializationException;
import tools.jackson.databind.ObjectMapper;
import com.sental.dto.request.CategoryCreateRequest;
import com.sental.dto.response.CategoryResponse;
import com.sental.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryController {

    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper) {
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    public String create(CategoryCreateRequest request) {
        CategoryResponse response = categoryService.create(request);
        return toJson(response);
    }

    public String findById(Long id) {
        CategoryResponse response = categoryService.findById(id);
        return toJson(response);
    }

    public String findAll() {
        List<CategoryResponse> list = categoryService.findAll();
        return toJson(list);
    }

    public void delete(Long id) {
        categoryService.delete(id);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (RuntimeException e) {
            throw new SerializationException("fail to create json" + e);
        }
    }
}