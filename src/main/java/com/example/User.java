package com.example;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;

@Entity(name = "users")
public class User extends PanacheEntity {
    @Column(unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;

    @Column
    public String name;

    @Column
    public Integer age;

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
}