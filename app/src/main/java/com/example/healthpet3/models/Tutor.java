package com.example.healthpet3.models;

import com.google.firebase.firestore.PropertyName;
public class Tutor {
    private String name, email, phone, clinic;
    @PropertyName("imageUrl")
    private String imageBase64;

    public Tutor(){

    }

    public Tutor(String name, String email, String phone, String clinic) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.clinic = clinic;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getClinic() {
        return clinic;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    @PropertyName("imageUrl")
    public String getImageBase64() {
        return imageBase64;
    }

    @PropertyName("imageUrl")
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}