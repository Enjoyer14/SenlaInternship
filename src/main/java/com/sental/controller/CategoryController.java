package com.sental.controller;

import com.sental.dto.request.update.CategoryUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.sental.dto.request.create.CategoryCreateRequest;
import com.sental.dto.response.CategoryResponse;
import com.sental.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "CategoryController is working!";
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody CategoryCreateRequest request) {
        return categoryService.create(request);
    }


    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable("id") Long id) {
        return categoryService.findById(id);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable("id") Long id, @RequestBody CategoryUpdateRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
    }

}