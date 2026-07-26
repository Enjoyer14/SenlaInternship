package com.sental.repository;

import com.sental.entity.User;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class UserRepository {

    private final Map<Long, User> store = new HashMap<>();
    private Long id = 1L;

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(id++);
        }
        store.put(user.getId(), user);
        return user;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}