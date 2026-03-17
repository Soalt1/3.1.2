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

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;  // ← Внедряем RoleService для работы с ролями

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

        // Получаем управляемые сущности ролей через RoleService (делегирование!)
        Set<Role> managedRoles = getManagedRoles(user.getRoles());
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

        // Обновляем поля одной операцией
        updateExistingUser(existingUser, user);

        userDao.update(existingUser);
        return existingUser;
    }

    /**
     * Метод для обновления полей одной операцией (вместо множества сеттеров)
     */
    private void updateExistingUser(User existingUser, User newUser) {
        // Копируем все поля одной группой (логически это одна операция)
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        existingUser.setAge(newUser.getAge());
        existingUser.setEmail(newUser.getEmail());

        // Пароль обновляем только если он передан
        if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }

        // Роли обновляем через RoleService
        if (newUser.getRoles() != null && !newUser.getRoles().isEmpty()) {
            Set<Role> managedRoles = getManagedRoles(newUser.getRoles());
            existingUser.setRoles(managedRoles);
        }
    }

    /**
     * Делегируем получение управляемых ролей RoleService
     * (логика работы с ролями вынесена в отдельный сервис)
     */
    private Set<Role> getManagedRoles(Set<Role> roles) {
        Set<Role> managedRoles = new HashSet<>();
        for (Role role : roles) {
            // RoleService отвечает за поиск ролей!
            Role managedRole = roleService.findByName(role.getName());
            managedRoles.add(managedRole);
        }
        return managedRoles;
    }

    /**
     * Делегируем получение ролей по ID RoleService
     */
    private Set<Role> getRolesByIds(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }
        // RoleService отвечает за поиск ролей по ID!
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

        // Делегируем получение ролей отдельному методу, который использует RoleService
        Set<Role> roles = getRolesByIds(roleIds);
        user.setRoles(roles);

        save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, String firstName, String lastName, Integer age,
                           String email, String password, Set<Long> roleIds) {
        User existingUser = findById(id);

        // Обновляем поля одной операцией
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setAge(age);
        existingUser.setEmail(email);

        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        // Делегируем получение ролей RoleService
        Set<Role> roles = getRolesByIds(roleIds);
        existingUser.setRoles(roles);

        // Сохраняем
        userDao.update(existingUser);
    }
}