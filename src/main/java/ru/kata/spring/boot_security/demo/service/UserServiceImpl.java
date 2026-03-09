package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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

        // Получаем управляемые сущности ролей из БД
        Set<Role> managedRoles = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (Role role : user.getRoles()) {
                // Ищем роль по имени в БД
                Role managedRole = roleService.findByName(role.getName());
                managedRoles.add(managedRole);
            }
        }
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

        // Копируем все свойства, кроме null и указанных полей
        copyNonNullProperties(user, existingUser, "id", "password", "roles");

        // Отдельно обрабатываем пароль
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Отдельно обрабатываем роли
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> managedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                Role managedRole = roleService.findByName(role.getName());
                managedRoles.add(managedRole);
            }
            existingUser.setRoles(managedRoles);
        }

        userDao.update(existingUser);
        return existingUser;
    }

    /**
     * Копирует свойства из источника в цель, игнорируя null значения и указанные поля
     */
    private void copyNonNullProperties(Object source, Object target, String... ignoreProperties) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source, ignoreProperties));
    }

    /**
     * Возвращает массив имен свойств, которые равны null или входят в список игнорируемых
     */
    private String[] getNullPropertyNames(Object source, String... ignoreProperties) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        // Добавляем игнорируемые поля
        for (String ignoreProperty : ignoreProperties) {
            emptyNames.add(ignoreProperty);
        }

        // Добавляем null поля
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
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

        Set<Role> roles = (roleIds == null || roleIds.isEmpty())
                ? new HashSet<>()
                : roleService.findByIds(roleIds);
        user.setRoles(roles);

        save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, String firstName, String lastName, Integer age,
                           String email, String password, Set<Long> roleIds) {
        User user = new User(firstName, lastName, age, email, password);
        user.setId(id);

        Set<Role> roles = (roleIds == null || roleIds.isEmpty())
                ? new HashSet<>()
                : roleService.findByIds(roleIds);
        user.setRoles(roles);

        update(user);
    }
}