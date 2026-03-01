package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> findAll() {
        return entityManager.createQuery("FROM Role", Role.class).getResultList();
    }

    @Override
    public Role findById(Long id) {
        return entityManager.find(Role.class, id);
    }

    @Override
    public Role findByName(String name) {
        TypedQuery<Role> query = entityManager.createQuery(
                "FROM Role WHERE name = :name", Role.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public Set<Role> findByIds(Set<Long> ids) {
        return ids.stream()
                .map(this::findById)
                .collect(Collectors.toSet());
    }

    @Override
    public void save(Role role) {
        entityManager.persist(role);
    }

    @Override
    public void initializeRoles() {
        if (findAll().isEmpty()) {
            save(new Role("ROLE_ADMIN"));
            save(new Role("ROLE_USER"));
        }
    }
}
