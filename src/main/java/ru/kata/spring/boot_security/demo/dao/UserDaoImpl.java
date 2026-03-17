package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class);
        return query.getResultList();  // ← Только User
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = entityManager.find(User.class, id);  // ← Только User
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();  // ← Только User
    }

    @Override
    public void save(User user) {
        entityManager.persist(user);  // ← Только User
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);  // ← Только User
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(entityManager::remove);  // ← Только User
    }

    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;  // ← Только User
    }
}