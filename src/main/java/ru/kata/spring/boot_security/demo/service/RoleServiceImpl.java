package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public List<Role> findAll() {
        return roleDao.findAll();
    }

    @Override
    public Role findById(Long id) {
        Role role = roleDao.findById(id);
        if (role == null) {
            throw new RuntimeException("Role not found with id: " + id);
        }
        return role;
    }

    @Override
    public Role findByName(String name) {
        Role role = roleDao.findByName(name);
        if (role == null) {
            throw new RuntimeException("Role not found with name: " + name);
        }
        return role;
    }

    @Override
    public Set<Role> findByIds(Set<Long> ids) {
        return roleDao.findByIds(ids);
    }

    @Override
    @Transactional
    public void initializeRoles() {
        roleDao.initializeRoles();
    }
}