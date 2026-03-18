package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserDao userDao,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public User save(User user) {
        if (userDao.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Кодируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Получаем ID ролей из присланных объектов
        Set<Role> managedRoles = getManagedRolesByIds(user.getRoles());
        user.setRoles(managedRoles);

        userDao.save(user);
        return user;
    }

    @Override
    @Transactional
    public User update(User user) {
        User existingUser = findById(user.getId());

        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userDao.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Обновляем поля
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAge(user.getAge());
        existingUser.setEmail(user.getEmail());

        // Пароль обновляем только если он передан
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Роли обновляем по ID
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> managedRoles = getManagedRolesByIds(user.getRoles());
            existingUser.setRoles(managedRoles);
        }

        userDao.update(existingUser);
        return existingUser;
    }

    /**
     * Получаем управляемые роли по ID (не по имени!)
     */
    private Set<Role> getManagedRolesByIds(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }

        // Собираем все ID ролей
        Set<Long> roleIds = roles.stream()
                .map(Role::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // Загружаем роли по ID через RoleService
        return roleService.findByIds(roleIds);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    @Override
    @Transactional
    public void createUser(String firstName, String lastName, Integer age,
                           String email, String password, Set<Long> roleIds) {
        User user = new User(firstName, lastName, age, email, password);

        // Получаем роли по ID
        Set<Role> roles = getRolesByIds(roleIds);
        user.setRoles(roles);

        save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, String firstName, String lastName, Integer age,
                           String email, String password, Set<Long> roleIds) {
        User existingUser = findById(id);

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setAge(age);
        existingUser.setEmail(email);

        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        Set<Role> roles = getRolesByIds(roleIds);
        existingUser.setRoles(roles);

        userDao.update(existingUser);
    }

    /**
     * Получаем роли по ID
     */
    private Set<Role> getRolesByIds(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }
        return roleService.findByIds(roleIds);
    }
}