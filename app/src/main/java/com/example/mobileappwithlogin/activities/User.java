package com.example.mobileappwithlogin.activities;

// User class for registering User object in Firebase database

public class User {

    public String fullName, age, email;

    public User(){

    }

    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }
}
