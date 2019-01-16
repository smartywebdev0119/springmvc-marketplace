package com.trade.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Blob;

public class User {

    @Min(0)
    private long id;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @Length(min = 3, max = 30)
    @Pattern(regexp = "[a-zA-Z0-9-]+")
    private String username;

    @NotNull
    @Length(min = 7, max = 30)
    @Pattern(regexp = "[a-zA-Z0-9-]+")
    private String password;

    @Length(min = 7, max = 30)
    @Pattern(regexp = "[a-zA-Z]+")
    private String name;

    @Length(min = 7, max = 30)
    @Pattern(regexp = "[a-zA-Z]+")
    private String surname;

    @NotNull
    @Length(min = 4, max = 10)
    private String role;

    private Blob image;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
