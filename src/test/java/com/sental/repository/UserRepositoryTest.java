package com.sental.repository;

import com.sental.config.TestPersistenceConfig;
import com.sental.entity.User;
import com.sental.enums.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(TestPersistenceConfig.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    void saveAndFindById() {
        User user = createUser("ivan", "ivan@mail.ru");
        Optional<User> found = userRepository.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("ivan", found.get().getUsername());
    }

    @Test
    @Transactional
    void findByUsername() {
        createUser("ivan", "ivan@mail.ru");
        entityManager.flush();
        Optional<User> found = userRepository.findByUsername("ivan");

        assertTrue(found.isPresent());
        assertEquals("ivan@mail.ru", found.get().getEmail());
    }

    @Test
    @Transactional
    void findByRoleAndEmailLike() {
        createUser("ivan", "ivan@mail.ru");
        entityManager.flush();
        List<User> users = userRepository.findByRoleAndEmail(UserRole.USER, "ivan@mail.ru");

        assertEquals(1, users.size());
        assertEquals("ivan", users.get(0).getUsername());
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("123");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}