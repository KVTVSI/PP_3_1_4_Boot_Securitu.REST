package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleServiceImp implements RoleService {

    final RoleRepository roleRepository;

    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional
    public Set<Role> getAll() {
        return new HashSet<>(roleRepository.findAll());
    }

    @Override
    @Transactional
    public Role findRoleByName(String role) {
        return roleRepository.findRoleByName(role);
    }

    @Override
    public Set<Role> getRole(Long id) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.getReferenceById(id));
        return roles;
    }
}
