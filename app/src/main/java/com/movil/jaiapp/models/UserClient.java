package com.movil.jaiapp.models;

import java.util.List;

public class UserClient {

    private String id;
    private String numSeller;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private List<Product> sellerProductsList;
    private List<Product> wishProductsList;
    private String role;
    private String created;
    private String updated;
    private int enable;

    public UserClient(){

    }

    public UserClient(String id, String numSeller, String name, String lastname, String email, String password, List<Product> sellerProductsList, List<Product> wishProductsList, String role, String created, String updated, int enable) {
        this.id = id;
        this.numSeller = numSeller;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.sellerProductsList = sellerProductsList;
        this.wishProductsList = wishProductsList;
        this.role = role;
        this.created = created;
        this.updated = updated;
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumSeller() {
        return numSeller;
    }

    public void setNumSeller(String numSeller) {
        this.numSeller = numSeller;
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

    public List<Product> getSellerProductsList() {
        return sellerProductsList;
    }

    public void setSellerProductsList(List<Product> sellerProductsList) {
        this.sellerProductsList = sellerProductsList;
    }

    public List<Product> getWishProductsList() {
        return wishProductsList;
    }

    public void setWishProductsList(List<Product> wishProductsList) {
        this.wishProductsList = wishProductsList;
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
