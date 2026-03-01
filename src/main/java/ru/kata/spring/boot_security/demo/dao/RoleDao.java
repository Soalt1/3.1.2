package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.List;
import java.util.Set;

public interface RoleDao {
    List<Role> findAll();
    Role findById(Long id);
    Role findByName(String name);
    Set<Role> findByIds(Set<Long> ids);
    void save(Role role);
    void initializeRoles();
}