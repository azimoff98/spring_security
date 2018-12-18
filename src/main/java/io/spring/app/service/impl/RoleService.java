package io.spring.app.service.impl;

import io.spring.app.model.Role;
import io.spring.app.repository.RoleRepository;
import io.spring.app.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class RoleService implements BaseService<Role> {


    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void save(Role role) {

        if(Objects.nonNull(role) && !role.getName().isEmpty())
            roleRepository.save(role);
    }

    @Override
    public Set<Role> getAll() {
        return (Set<Role>)roleRepository.findAll();
    }

    @Override
    public Role getById(Long id) {

        if(roleRepository.findById(id).isPresent()){
            return roleRepository.findById(id).get();
        }

        return null;

    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }
}
