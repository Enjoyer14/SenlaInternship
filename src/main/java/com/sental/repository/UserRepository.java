package com.sental.repository;

import com.sental.entity.User;
import liquibase.pro.packaged.A;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private final Map<Long, User> store = new HashMap<>();
    private AtomicLong id = new AtomicLong(1);

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(id.getAndIncrement());
        }
        store.put(user.getId(), user);
        return user;
    }

    public void delete(Long id) {
        store.remove(id);
    }
}