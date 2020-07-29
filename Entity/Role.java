package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "role_table")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;
    @Column(name = "role_name")
    private String roleName;
    //作为被维护端，只需要设置mappedBy属性，其值与User中对应List类型变量名相同
    //@JsonBackReference可以避免属性被json序列化，出现死循环
    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_auth", //name是表名
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns =@JoinColumn(name = "auth_id")
    )
    private List<Authority> authorities;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
