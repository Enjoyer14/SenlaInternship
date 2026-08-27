package com.sental.service;

import com.sental.dto.request.create.CategoryCreateRequest;
import com.sental.dto.request.update.CategoryUpdateRequest;
import com.sental.dto.response.CategoryResponse;
import com.sental.entity.Category;
import com.sental.exception.EntityNotFound;
import com.sental.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private Category parentCategory;
    private CategoryCreateRequest createRequest;
    private CategoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        parentCategory = new Category();
        parentCategory.setId(10L);
        parentCategory.setName("Parent");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
        testCategory.setParent(parentCategory);

        createRequest = new CategoryCreateRequest();
        createRequest.setName("Clothing");
        createRequest.setParentId(10L);

        updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Footwear");
        updateRequest.setParentId(null);
    }

    @Test
    void create_withParent_shouldSaveAndReturnResponse() {
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        CategoryResponse response = categoryService.create(createRequest);
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Clothing", response.getName());
        assertEquals(10L, response.getParentId());

        verify(categoryRepository, times(1)).findById(10L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void create_withoutParent_shouldSaveWithNullParent() {
        createRequest.setParentId(null);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(3L);
            return saved;
        });
        CategoryResponse response = categoryService.create(createRequest);
        assertNotNull(response);
        assertEquals(3L, response.getId());
        assertEquals("Clothing", response.getName());
        assertNull(response.getParentId());

        verify(categoryRepository, never()).findById(any());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void create_withInvalidParent_shouldThrowEntityNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        createRequest.setParentId(999L);
        assertThrows(EntityNotFound.class, () -> categoryService.create(createRequest));

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findById_whenExists_shouldReturnResponse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        CategoryResponse response = categoryService.findById(1L);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Electronics", response.getName());
        assertEquals(10L, response.getParentId());

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void findById_whenNotFound_shouldThrowEntityNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> categoryService.findById(99L));

        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void findAll_shouldReturnListOfResponses() {
        Category another = new Category();
        another.setId(2L);
        another.setName("Books");
        another.setParent(null);
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory, another));
        List<CategoryResponse> responses = categoryService.findAll();

        assertEquals(2, responses.size());
        assertEquals("Electronics", responses.get(0).getName());
        assertEquals("Books", responses.get(1).getName());
        assertNull(responses.get(1).getParentId());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void update_withNewParent_shouldUpdateAndReturnResponse() {
        Category newParent = new Category();
        newParent.setId(20L);
        newParent.setName("NewParent");

        CategoryUpdateRequest updateWithParent = new CategoryUpdateRequest();
        updateWithParent.setName("UpdatedName");
        updateWithParent.setParentId(20L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findById(20L)).thenReturn(Optional.of(newParent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CategoryResponse response = categoryService.update(1L, updateWithParent);

        assertNotNull(response);
        assertEquals("UpdatedName", response.getName());
        assertEquals(20L, response.getParentId());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(20L);
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void update_withoutParent_shouldSetParentToNull() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CategoryResponse response = categoryService.update(1L, updateRequest);
        assertNotNull(response);
        assertEquals("Footwear", response.getName());
        assertNull(response.getParentId());

        verify(categoryRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(categoryRepository); // Ensures no second findById call occurred
        assertNull(testCategory.getParent());
    }

    @Test
    void update_whenCategoryNotFound_shouldThrowEntityNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> categoryService.update(99L, updateRequest));

        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_whenParentNotFound_shouldThrowEntityNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        updateRequest.setParentId(999L);
        assertThrows(EntityNotFound.class, () -> categoryService.update(1L, updateRequest));

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_whenExists_shouldCallRepositoryDelete() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(testCategory);
        categoryService.delete(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    void delete_whenNotFound_shouldThrowEntityNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> categoryService.delete(99L));

        verify(categoryRepository, times(1)).findById(99L);
        verify(categoryRepository, never()).delete(any());
    }
}