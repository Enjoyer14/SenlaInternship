package com.sental.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sental.config.TestPersistenceConfig;
import com.sental.config.WebConfig;
import com.sental.dto.request.create.CategoryCreateRequest;
import com.sental.dto.request.update.CategoryUpdateRequest;
import com.sental.dto.response.CategoryResponse;
import com.sental.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        TestPersistenceConfig.class,
        WebConfig.class
})
@Transactional
class CategoryControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void healthCheck_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/categories/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("CategoryController is working!"));
    }

    @Test
    void create_shouldReturnCreatedCategory() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Furniture");
        request.setParentId(null);

        MvcResult result = mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Furniture")))
                .andExpect(jsonPath("$.parentId", nullValue()))
                .andReturn();

        CategoryResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);
        Category saved = entityManager.find(Category.class, response.getId());
        assertNotNull(saved);
        assertEquals("Furniture", saved.getName());
        assertNull(saved.getParent());
    }

    @Test
    void create_withParent_shouldSetParent() throws Exception {
        Category parent = new Category();
        parent.setName("Electronics");
        entityManager.persist(parent);
        entityManager.flush();

        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Laptops");
        request.setParentId(parent.getId());

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Laptops")))
                .andExpect(jsonPath("$.parentId", is(parent.getId().intValue())));
    }

    @Test
    void create_withInvalidParent_shouldReturnNotFound() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName("Invalid");
        request.setParentId(999L);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnCategory() throws Exception {
        Category category = new Category();
        category.setName("Books");
        entityManager.persist(category);
        entityManager.flush();

        mockMvc.perform(get("/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(category.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Books")))
                .andExpect(jsonPath("$.parentId", nullValue()));
    }

    @Test
    void findById_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnList() throws Exception {
        Category cat1 = new Category();
        cat1.setName("Food");
        Category cat2 = new Category();
        cat2.setName("Drinks");
        entityManager.persist(cat1);
        entityManager.persist(cat2);
        entityManager.flush();

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].name", hasItems("Food", "Drinks")));
    }

    @Test
    void update_shouldModifyCategory() throws Exception {
        Category category = new Category();
        category.setName("OldName");
        entityManager.persist(category);
        entityManager.flush();

        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("NewName");
        updateRequest.setParentId(null);

        mockMvc.perform(put("/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(category.getId().intValue())))
                .andExpect(jsonPath("$.name", is("NewName")))
                .andExpect(jsonPath("$.parentId", nullValue()));

        Category updated = entityManager.find(Category.class, category.getId());
        assertEquals("NewName", updated.getName());
        assertNull(updated.getParent());
    }

    @Test
    void update_withNewParent_shouldChangeParent() throws Exception {
        Category parent = new Category();
        parent.setName("Parent");
        Category child = new Category();
        child.setName("Child");
        child.setParent(parent);
        entityManager.persist(parent);
        entityManager.persist(child);
        entityManager.flush();

        Category newParent = new Category();
        newParent.setName("NewParent");
        entityManager.persist(newParent);
        entityManager.flush();

        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("UpdatedChild");
        updateRequest.setParentId(newParent.getId());

        mockMvc.perform(put("/categories/{id}", child.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("UpdatedChild")))
                .andExpect(jsonPath("$.parentId", is(newParent.getId().intValue())));
    }

    @Test
    void update_withInvalidParent_shouldReturn404() throws Exception {
        Category category = new Category();
        category.setName("Test");
        entityManager.persist(category);
        entityManager.flush();

        CategoryUpdateRequest updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Test");
        updateRequest.setParentId(999L);

        mockMvc.perform(put("/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldRemoveCategory() throws Exception {
        Category category = new Category();
        category.setName("ToDelete");
        entityManager.persist(category);
        entityManager.flush();

        mockMvc.perform(delete("/categories/{id}", category.getId())).andExpect(status().isNoContent());

        assertNull(entityManager.find(Category.class, category.getId()));
    }

    @Test
    void delete_whenNotFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/categories/999")).andExpect(status().isNotFound());
    }
}