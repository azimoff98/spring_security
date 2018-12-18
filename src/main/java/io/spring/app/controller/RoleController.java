package io.spring.app.controller;

import io.spring.app.model.Role;
import io.spring.app.service.impl.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    public void save(@RequestBody Role role){
        roleService.save(role);
    }

    @GetMapping("/{id}")
    public Role getById(@PathVariable  Long id){
        return roleService.getById(id);
    }

    @GetMapping("/getAll")
    public Set<Role> getAll(){
        return roleService.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        roleService.deleteById(id);
    }

}
