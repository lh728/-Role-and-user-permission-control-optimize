package com.example.Dao;

import com.example.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleDao extends JpaRepository<Role, Integer> {
    List<Role> findAllByRoleName(String roleName);
    List<Role> findAllByRoleId(Integer roleId);
    void deleteByRoleId(Integer roleId);
}
