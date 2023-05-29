package com.example.cutaway;

public class BusinessCard {
    private String encoded;
    private String firstName;
    private String lastName;
    private String company;
    private String phone;
    private String email;

    public BusinessCard(String firstName, String lastName, String company, String phone, String email) {
        this.encoded = "";
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.phone = phone;
        this.email = email;
    }
    public BusinessCard(String encoded, String firstName, String lastName, String company, String phone, String email) {
        this.encoded = encoded;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.phone = phone;
        this.email = email;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEncoded() {
        return encoded;
    }
    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }
}
