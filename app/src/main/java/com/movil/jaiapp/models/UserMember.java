package com.movil.jaiapp.models;

import java.util.List;

public class UserMember {

    private String numMember;
    private String name;
    private String lastname;
    private int phoneNumber;
    private String email;
    private String password;
    private String image;
    private List<Product> productsList;
    private String role;
    private String created;
    private String updated;
    private int enable;

    public UserMember(String numMember, String name, String lastname, int phoneNumber, String email, String password, String image, List<Product> productsList, String role, String created, String updated, int enable) {
        this.numMember = numMember;
        this.name = name;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.image = image;
        this.productsList = productsList;
        this.role = role;
        this.created = created;
        this.updated = updated;
        this.enable = enable;
    }

    public String getNumMember() {
        return numMember;
    }

    public void setNumMember(String numMember) {
        this.numMember = numMember;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
