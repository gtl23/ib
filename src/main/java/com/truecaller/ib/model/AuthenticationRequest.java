package com.truecaller.ib.model;

public class AuthenticationRequest {

    private String phone;

    private String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
