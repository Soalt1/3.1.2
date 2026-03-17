package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;  // ← Работает только со своим DAO

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();  // ← Только Role
    }

    @Override
    public Role findById(Long id) {
        return roleDao.findById(id)  // ← Только Role
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Override
    public Role findByName(String name) {
        return roleDao.findByName(name)  // ← Только Role
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
    }

    @Override
    public Set<Role> findByIds(Set<Long> ids) {
        return roleDao.findByIds(ids);  // ← Только Role
    }

    @Override
    @Transactional
    public void save(Role role) {
        roleDao.save(role);  // ← Только Role
    }

    @Override
    @Transactional
    public void initializeRoles() {
        if (findAll().isEmpty()) {
            save(new Role("ROLE_ADMIN"));  // ← Только Role
            save(new Role("ROLE_USER"));   // ← Только Role
        }
    }
}