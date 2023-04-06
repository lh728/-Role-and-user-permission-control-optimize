package com.example.Dao;

import com.example.Entity.Authority; 
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityDao extends JpaRepository<Authority, Integer> {
    List<Authority> findAllByAuthorityName(String authorityName);
    void deleteByAuthorityName(String authorityName);
    List<Authority> findAllByAuthorityId(Integer authorityId);   
}
